package kin.sdk.core;


import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Request<T> {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Handler mainHandler;
    private final Callable<T> callable;
    private boolean cancelled;
    private boolean executed;
    private Future<?> future;
    private ResultCallback<T> resultCallback;

    Request(Callable<T> callable) {
        this.callable = callable;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    synchronized public void run(ResultCallback<T> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback cannot be null.");
        }
        if (executed) {
            throw new IllegalStateException("Request already running.");
        }
        if (cancelled) {
            throw new IllegalStateException("Request already cancelled.");
        }
        executed = true;
        submitFuture(callable, callback);
    }

    private void submitFuture(final Callable<T> callable, ResultCallback<T> callback) {
        this.resultCallback = callback;
        future = executorService.submit(() -> {
            try {
                final T result = callable.call();
                executeOnMainThreadIfNotInterrupted(() -> resultCallback.onResult(result));
            } catch (final Exception e) {
                executeOnMainThreadIfNotInterrupted(() -> resultCallback.onError(e));
            }
        });
    }

    private synchronized void executeOnMainThreadIfNotInterrupted(Runnable runnable) {
        if (!cancelled) {
            mainHandler.post(runnable);
        }
    }

    synchronized public void cancel() {
        if (!cancelled) {
            cancelled = true;
            future.cancel(true);
            future = null;
            mainHandler.removeCallbacksAndMessages(null);
            mainHandler.post(() -> resultCallback = null);
        }
    }

}
