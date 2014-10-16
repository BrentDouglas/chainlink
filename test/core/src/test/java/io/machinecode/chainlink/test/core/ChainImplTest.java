package io.machinecode.chainlink.test.core;

import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.test.core.execution.Reference;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChainImplTest {

    @Test
    public void resolveTest() throws Exception {
        final ChainImpl<Void> first = new ChainImpl<Void>();
        final ChainImpl<Void> second = new ChainImpl<Void>();
        final ChainImpl<Void> third = new ChainImpl<Void>();

        first.link(second);
        second.link(third);

        Assert.assertFalse(first.isDone());
        Assert.assertFalse(second.isDone());
        Assert.assertFalse(third.isDone());

        final CountDownLatch latch = new CountDownLatch(1);
        final Reference<Boolean> done = new Reference<Boolean>(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    third.get(1, TimeUnit.SECONDS);
                    done.set(true);
                } catch (final Exception e) {
                    // Swallow
                } finally {
                    done.set(true);
                }
            }
        }).start();
        latch.await();
        
        second.resolve(null);

        Assert.assertFalse(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertFalse(third.isDone());
        Assert.assertFalse(done.get());

        first.resolve(null);

        Assert.assertTrue(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertFalse(third.isDone());
        Assert.assertFalse(done.get());

        third.resolve(null);

        Assert.assertTrue(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertTrue(third.isDone());
        Assert.assertFalse(done.get());

        third.link(new ResolvedChain<Void>(null));
        Thread.sleep(100);
        Assert.assertTrue(done.get());
    }

    @Test
    public void rejectTest() throws Exception {
        final ChainImpl<Void> first = new ChainImpl<Void>();
        final ChainImpl<Void> second = new ChainImpl<Void>();
        final ChainImpl<Void> third = new ChainImpl<Void>();

        first.link(second);
        second.link(third);

        Assert.assertFalse(first.isDone());
        Assert.assertFalse(second.isDone());
        Assert.assertFalse(third.isDone());

        final CountDownLatch latch = new CountDownLatch(1);
        final Reference<Boolean> done = new Reference<Boolean>(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    third.get(1, TimeUnit.SECONDS);
                    done.set(true);
                } catch (final Exception e) {
                    // Swallow
                } finally {
                    done.set(true);
                }
            }
        }).start();
        latch.await();

        second.reject(null);

        Assert.assertFalse(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertFalse(third.isDone());
        Assert.assertFalse(done.get());

        first.reject(null);

        Assert.assertTrue(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertFalse(third.isDone());
        Assert.assertFalse(done.get());

        third.reject(null);

        Assert.assertTrue(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertTrue(third.isDone());
        Assert.assertFalse(done.get());

        third.link(new ResolvedChain<Void>(null));
        Thread.sleep(100);
        Assert.assertTrue(done.get());
    }

    @Test
    public void cancelTest() throws Exception {
        final ChainImpl<Void> first = new ChainImpl<Void>();
        final ChainImpl<Void> second = new ChainImpl<Void>();
        final ChainImpl<Void> third = new ChainImpl<Void>();

        first.link(second);
        second.link(third);

        Assert.assertFalse(first.isDone());
        Assert.assertFalse(second.isDone());
        Assert.assertFalse(third.isDone());

        final CountDownLatch latch = new CountDownLatch(1);
        final Reference<Boolean> done = new Reference<Boolean>(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    third.get(1, TimeUnit.SECONDS);
                    done.set(true);
                } catch (final Exception e) {
                    // Swallow
                } finally {
                    done.set(true);
                }
            }
        }).start();
        latch.await();

        third.cancel(true);

        Assert.assertFalse(first.isDone());
        Assert.assertFalse(second.isDone());
        Assert.assertTrue(third.isDone());
        Assert.assertFalse(done.get());

        second.cancel(true);

        Assert.assertFalse(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertTrue(third.isDone());
        Assert.assertFalse(done.get());

        first.cancel(true);

        Assert.assertTrue(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertTrue(third.isDone());
        Assert.assertFalse(done.get());


        third.link(new ResolvedChain<Void>(null));
        Thread.sleep(100);
        Assert.assertTrue(done.get());
    }
}
