package kin.sdk.core;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import android.os.Looper;
import android.support.test.runner.AndroidJUnit4;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.function.Consumer;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RequestTest {

    private static final int TASK_DURATION_MILLIS = 50;
    private static final int TIMEOUT_DURATION_MILLIS = 100;

    private <T> void runRequest(Callable<T> task, Consumer<T> onResultCallback, Consumer<Exception> onErrorCallback)
        throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Request<T> dummyRequest = new Request<>(() -> {
            T result = task.call();
            Thread.sleep(TASK_DURATION_MILLIS);
            return result;
        });
        dummyRequest.run(new ResultCallback<T>() {
            @Override
            public void onResult(T result) {
                if (onResultCallback != null) {
                    onResultCallback.accept(result);
                }
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                if (onErrorCallback != null) {
                    onErrorCallback.accept(e);
                }
                latch.countDown();
            }
        });
        assertTrue(latch.await(TIMEOUT_DURATION_MILLIS, TimeUnit.MILLISECONDS));
    }

    @Test
    public void run_verifyExpectedResult() throws InterruptedException {
        String expectedResult = "ExpectedResult";
        runRequest(
            () -> expectedResult,
            result -> assertEquals(expectedResult, result),
            null
        );
    }

    @Test
    public void run_verifyCorrectThreads() throws InterruptedException {
        AtomicLongArray threadIds = new AtomicLongArray(2);
        runRequest(() -> {
                threadIds.set(0, Thread.currentThread().getId());
                return null;
            },
            result -> threadIds.set(1, Thread.currentThread().getId()),
            null
        );

        long mainThreadId = Looper.getMainLooper().getThread().getId();
        assertNotEquals(mainThreadId, threadIds.get(0));
        assertEquals(mainThreadId, threadIds.get(1));
    }

    @Test
    public void run_verifyExceptionPropagation() throws InterruptedException {
        Exception expectedException = new Exception("some exception");
        runRequest(() -> {
                throw expectedException;
            }, null
            , e -> assertEquals(expectedException, e));
    }

    @Test
    public void run_cancelBeforeRun() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Request<Object> dummyRequest = new Request<>(Object::new);
        dummyRequest.run(new ResultCallback<Object>() {
            @Override
            public void onResult(Object result) {
                Assert.fail();
            }

            @Override
            public void onError(Exception e) {
                Assert.fail();
            }
        });
        dummyRequest.cancel();
        latch.await(TIMEOUT_DURATION_MILLIS, TimeUnit.MILLISECONDS);
    }

    @Test
    public void run_cancelAfterRun() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        boolean[] threadInterrupted = new boolean[]{false};
        Request<Object> dummyRequest = new Request<>(() -> {
            try {
                Thread.sleep(TASK_DURATION_MILLIS);
            } catch (InterruptedException ie) {
                threadInterrupted[0] = true;
                latch.countDown();
            }
            return new Object();
        });
        dummyRequest.run(new ResultCallback<Object>() {
            @Override
            public void onResult(Object result) {
            }

            @Override
            public void onError(Exception e) {
            }
        });
        Thread.sleep(TASK_DURATION_MILLIS / 2);
        dummyRequest.cancel();
        assertTrue(latch.await(TIMEOUT_DURATION_MILLIS, TimeUnit.MILLISECONDS));
        assertEquals(true, threadInterrupted[0]);
    }
}