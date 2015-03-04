package io.machinecode.chainlink.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkTest extends Assert {

    @Test
    public void testChainlink() throws Exception {
        final AtomicBoolean done = new AtomicBoolean(false);
        final CountDownLatch before = new CountDownLatch(1);
        final CountDownLatch after = new CountDownLatch(1);
        final AtomicReference<Exception> exception = new AtomicReference<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                before.countDown();
                try {
                    Chainlink.getEnvironment();
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    done.set(true);
                    after.countDown();
                }
            }
        }).start();
        before.await(2, TimeUnit.SECONDS);
        assertFalse(done.get());
        final TestEnvironment environment = new TestEnvironment(null);
        Chainlink.setEnvironment(environment);
        after.await(2, TimeUnit.SECONDS);
        assertTrue(done.get());
        assertSame(environment, Chainlink.getEnvironment());
    }
}
