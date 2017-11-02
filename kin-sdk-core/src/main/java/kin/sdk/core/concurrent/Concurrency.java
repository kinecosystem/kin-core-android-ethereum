package kin.sdk.core.concurrent;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shaybaz on 01/11/2017.
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

    private Concurrency() {
        mainHandler = new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
    }
}
