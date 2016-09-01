package org.mockitousage.verification;

import org.mockitousage.IMethods;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;

class DelayedExecution {
    private final ScheduledExecutorService executor;
    private final IMethods mock;
    private final int delay;
    private final ArrayList<Character> invocations = new ArrayList<Character>();

    public DelayedExecution(ScheduledExecutorService executor, IMethods mock, int delay) {
        this.executor = executor;
        this.mock = mock;
        this.delay = delay;
    }

    public void recordAsyncCall(char c) {
        invocations.add(c);
    }

    public void allAsyncCallsStarted() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(invocations.size());
        for (final Character invocation : invocations) {
            executor.execute(runnable(countDownLatch, invocation));
        }
        countDownLatch.await();
    }

    private Runnable runnable(final CountDownLatch countDownLatch, final Character invocation) {
        return new Runnable() {
            @Override
            public void run() {
                countDownLatch.countDown();
                sleep();
                mock.oneArg(invocation.charValue());
            }
        };
    }

    private void sleep() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
