package kin.sdk.core.concurrent;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kin.sdk.core.ResultCallback;

/**
 * For calling method to run on ui thread or main thread
 * Background thread using default CachedThreadPool
 */

public class Concurrency {

    private ExecutorService executorService;
    private static Concurrency instance;
    private Handler mainHandler;

    public static synchronized Concurrency getInstance() {
        if (instance == null) {
            instance = new Concurrency();
        }
        return instance;
    }

    public void executeOnBackground(Runnable task) {
        executorService.execute(task);
    }

    public void executeOnMainThread(Runnable task) {
        mainHandler.post(task);
    }

    public <T> void execute(final Callable<T> callable, final ResultCallback<T> callback) {
        executeOnBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    final T result = callable.call();
                    executeOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResult(result);
                        }
                    });
                } catch (final Exception e) {
                    executeOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }

    private Concurrency() {
        mainHandler = new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
    }
}
