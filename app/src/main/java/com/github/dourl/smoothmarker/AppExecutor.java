package com.github.dourl.smoothmarker;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

public class AppExecutor {
    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainThread;

    private static class AppExecutorHolder {
        public static AppExecutor instance = new AppExecutor();
    }

    public AppExecutor(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public AppExecutor() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(5),
                new MainThreadExecutor());
    }

    public static AppExecutor getInstance() {
        return AppExecutorHolder.instance;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor networkIO() {
        return networkIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
