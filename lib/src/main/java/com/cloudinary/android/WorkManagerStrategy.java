package com.cloudinary.android;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.cloudinary.android.callback.UploadStatus;
import com.cloudinary.android.policy.UploadPolicy;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.BackoffPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class WorkManagerStrategy implements BackgroundRequestStrategy {
    public static final String REQUEST_ID_PREFIX = "request_id";
    public static final String REQUEST_START_TIMESTAMP_PREFIX = "s_timestamp";
    public static final String TAG_KEY_VALUE_SPLITTER = ":";
    private static final String WORK_MANAGER_TAG = "CLD_WORKERS";
    private static final String TAG = "WorkManagerStrategy";
    private static final Map<String, WeakReference<Thread>> threads = new ConcurrentHashMap<>();
    private static final Object threadsMapLockObject = new Object();


    @Override
    public void init(Context context) {
        // none required
    }

    @Override
    public void doDispatch(UploadRequest request) {
        WorkManager.getInstance().enqueue(adapt(request));
    }

    private static OneTimeWorkRequest adapt(UploadRequest request) {

        WorkManagerRequestParams data = new WorkManagerRequestParams(new Data.Builder().build());
        request.populateParamsFromFields(data);

        UploadPolicy policy = request.getUploadPolicy();

        Constraints.Builder constraintsBuilder = new Constraints.Builder()
                .setRequiresCharging(policy.isRequiresCharging())
                .setRequiredNetworkType(adaptNetworkType(policy.getNetworkType()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            constraintsBuilder.setRequiresDeviceIdle(policy.isRequiresIdle());
        }

        OneTimeWorkRequest huh = new OneTimeWorkRequest.Builder(RequestWorker.class)
                .setInitialDelay(request.getTimeWindow().getMinLatencyOffsetMillis(), TimeUnit.MILLISECONDS)
                .setInputData(data.getData())
                .setBackoffCriteria(adaptBackoffPolicy(policy.getBackoffPolicy()), policy.getBackoffMillis(), TimeUnit.MILLISECONDS)
                .addTag(WORK_MANAGER_TAG)
                // workaround: store data inside tags since it's the only available data when querying tasks:
                .addTag(REQUEST_ID_PREFIX + TAG_KEY_VALUE_SPLITTER + request.getRequestId())
                .addTag(REQUEST_START_TIMESTAMP_PREFIX + TAG_KEY_VALUE_SPLITTER + System.currentTimeMillis() + request.getTimeWindow().getMinLatencyOffsetMillis())
                .setConstraints(constraintsBuilder.build())
                .build();

        return huh;
    }

    private static NetworkType adaptNetworkType(UploadPolicy.NetworkType networkType) {
        switch (networkType) {

            case NONE:
                return NetworkType.NOT_REQUIRED;
            case ANY:
                return NetworkType.CONNECTED;
            case UNMETERED:
                return NetworkType.UNMETERED;
        }

        return NetworkType.CONNECTED;
    }

    @Override
    public void executeRequestsNow(int howMany) {
        // work manager doesn't easily support modifying existing tasks - it requires cancellation
        // and re-enqueuing, which isn't possible without saving all the tasks' data ourselves.
        Logger.d(TAG, "Job scheduled started 0 requests.");
    }

    @Override
    public boolean cancelRequest(String requestId) {
        boolean cancelled = false;

        WorkInfo requestInfo = getById(requestId);
        if (requestInfo != null) {
            WorkManager.getInstance().cancelWorkById(requestInfo.getId());
            cancelled = true;
        }

        // just in case..
        killThread(requestId);

        Logger.i(TAG, String.format("Cancelling request %s, success: %s", requestId, cancelled));
        return cancelled;
    }

    private WorkInfo getById(String requestId) {
        List<WorkInfo> requestsInfo = getWorkStatuses();
        for (WorkInfo requestInfo : requestsInfo) {
            for (String tag : requestInfo.getTags()) {
                if (tag.startsWith(REQUEST_ID_PREFIX) && requestId.equals(tag.split(TAG_KEY_VALUE_SPLITTER)[1])) {
                    return requestInfo;
                }
            }
        }

        return null;
    }

    private List<WorkInfo> getWorkStatuses() {
        List<WorkInfo> value = WorkManager.getInstance().getWorkInfosByTagLiveData(WORK_MANAGER_TAG).getValue();
        return value != null ? value : Collections.<WorkInfo>emptyList();
    }

    @Override
    public int cancelAllRequests() {
        WorkManager.getInstance().cancelAllWorkByTag(WORK_MANAGER_TAG);
        killAllThreads();
        return -1;
    }

    @Override
    public int getPendingImmediateJobsCount() {
        int pending = 0;
        List<WorkInfo> requestsInfo = getWorkStatuses();
        for (WorkInfo requestInfo : requestsInfo) {
            if (isImmediate(requestInfo)) {
                pending++;
            }
        }

        return pending;
    }

    @Override
    public int getRunningJobsCount() {
        int count = 0;
        List<WorkInfo> statuses = getWorkStatuses();
        for (WorkInfo status : statuses) {
            if (status.getState() == WorkInfo.State.RUNNING) {
                count++;
            }
        }

        return count;
    }

    private boolean isSoonButNotImmediate(WorkInfo requestInfo) {
        Long start = getStartTimeMillis(requestInfo);
        return start != null && start > System.currentTimeMillis() + IMMEDIATE_THRESHOLD &&
                start < System.currentTimeMillis() + SOON_THRESHOLD;

    }

    private Long getStartTimeMillis(WorkInfo requestInfo) {
        for (String tag : requestInfo.getTags()) {
            if (tag.startsWith(REQUEST_START_TIMESTAMP_PREFIX)) {
                return Long.parseLong(tag.split(TAG_KEY_VALUE_SPLITTER)[1]);
            }
        }

        return null;
    }

    private static Worker.Result adapt(UploadStatus result) {
        switch (result) {
            case FAILURE:
                return Worker.Result.failure();
            case SUCCESS:
                return Worker.Result.success();
            case RESCHEDULE:
                return Worker.Result.retry();
        }

        // unexpected result, we don't want to retry because we have no idea why it failed.
        return Worker.Result.failure();
    }

    private boolean isImmediate(WorkInfo requestInfo) {
        Long start = getStartTimeMillis(requestInfo);
        return start != null && start < System.currentTimeMillis() + IMMEDIATE_THRESHOLD;
    }

    private void killThread(String requestId) {
        synchronized (threadsMapLockObject) {
            WeakReference<Thread> ref = threads.remove(requestId);
            if (ref != null) {
                Thread thread = ref.get();
                if (thread != null) {
                    thread.interrupt();
                }

                ref.clear();
            }
        }
    }

    private void killAllThreads() {
        synchronized (threadsMapLockObject) {
            for (String requestId : threads.keySet()) {
                WeakReference<Thread> ref = threads.get(requestId);
                Thread thread = ref.get();

                if (thread != null) {
                    thread.interrupt();
                }

                ref.clear();
            }

            threads.clear();
        }
    }

    private static BackoffPolicy adaptBackoffPolicy(UploadPolicy.BackoffPolicy backoffPolicy) {
        switch (backoffPolicy) {
            case LINEAR:
                return BackoffPolicy.LINEAR;
            case EXPONENTIAL:
            default:
                return BackoffPolicy.EXPONENTIAL;
        }
    }

    public static class RequestWorker extends Worker {
        public RequestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Worker.Result doWork() {
            String requestId = getInputData().getString("requestId");
            registerThread(requestId);
            try {
                WorkManagerRequestParams params = new WorkManagerRequestParams(getInputData());
                params.putInt(DefaultRequestProcessor.ERROR_COUNT_PARAM, getRunAttemptCount());
                UploadStatus result = MediaManager.get().processRequest(this.getApplicationContext(), params);
                return adapt(result);
            } finally {
                unregisterThread(requestId);
            }
        }

        private void unregisterThread(String requestId) {
            synchronized (threadsMapLockObject) {
                WeakReference<Thread> removed = threads.remove(requestId);
                if (removed != null) {
                    removed.clear();
                }
            }
        }

        private void registerThread(String requestId) {
            if (requestId != null) {
                synchronized (threadsMapLockObject) {
                    threads.put(requestId, new WeakReference<>(Thread.currentThread()));
                }
            }
        }
    }

    private static final class WorkManagerRequestParams implements RequestParams {
        private Data data;

        private WorkManagerRequestParams(Data data) {
            this.data = data;
        }

        @Override
        public void putString(String key, String value) {
            data = new Data.Builder().putAll(data).putString(key, value).build();
        }

        @Override
        public void putInt(String key, int value) {
            data = new Data.Builder().putAll(data).putInt(key, value).build();
        }

        @Override
        public void putLong(String key, long value) {
            data = new Data.Builder().putAll(data).putLong(key, value).build();
        }

        @Override
        public void putBoolean(String key, boolean value) {

        }

        @Override
        public String getString(String key, String defaultValue) {
            String string = data.getString(key);
            return string != null ? string : defaultValue;
        }

        @Override
        public int getInt(String key, int defaultValue) {
            return data.getInt(key, defaultValue);
        }

        @Override
        public long getLong(String key, long defaultValue) {
            return data.getLong(key, defaultValue);
        }

        @Override
        public boolean getBoolean(String key, boolean defaultValue) {
            return false;
        }

        public Data getData() {
            return data;
        }
    }
}
