package io.machinecode.chainlink.test.core;

import io.machinecode.chainlink.core.then.AllChain;
import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.then.RejectedChain;
import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.test.core.execution.Reference;
import io.machinecode.then.api.Promise;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AllChainTest {

    @Test
    public void resolveTest() throws Exception {
        final ChainImpl<Void> p1 = new ChainImpl<Void>();
        final ChainImpl<Void> p2 = new ChainImpl<Void>();
        final ChainImpl<Void> p3 = new ChainImpl<Void>();
        final ChainImpl<Void> p4 = new ChainImpl<Void>();
        final AllChain<Promise<?,?,?>> all = new AllChain<Promise<?,?,?>>(p1, p2, p3, p4);

        Assert.assertFalse(p1.isDone());
        Assert.assertFalse(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());

        final CountDownLatch latch = new CountDownLatch(1);
        final Reference<Boolean> done = new Reference<Boolean>(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    all.get(1, TimeUnit.SECONDS);
                    done.set(true);
                } catch (final Exception e) {
                    // Swallow
                } finally {
                    done.set(true);
                }
            }
        }).start();
        latch.await();
        Thread.sleep(100);

        p1.resolve(null);

        Assert.assertTrue(p1.isDone());
        Assert.assertFalse(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertFalse(done.get());

        p2.resolve(null);

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertFalse(done.get());

        p3.resolve(null);

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertTrue(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertFalse(done.get());

        p4.resolve(null);

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertTrue(p3.isDone());
        Assert.assertTrue(p4.isDone());
        Assert.assertTrue(all.isDone());
        Assert.assertFalse(done.get());

        final ResolvedChain<Void> after = new ResolvedChain<Void>(null);
        p1.link(after);
        p2.link(after);
        p3.link(after);
        p4.link(after);

        Thread.sleep(100);
        Assert.assertTrue(done.get());
    }

    @Test
    public void rejectTest() throws Exception {
        final ChainImpl<Void> p1 = new ChainImpl<Void>();
        final ChainImpl<Void> p2 = new ChainImpl<Void>();
        final ChainImpl<Void> p3 = new ChainImpl<Void>();
        final ChainImpl<Void> p4 = new ChainImpl<Void>();
        final AllChain<Promise<?,?,?>> all = new AllChain<Promise<?,?,?>>(p1, p2, p3, p4);

        Assert.assertFalse(p1.isDone());
        Assert.assertFalse(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());

        final CountDownLatch latch = new CountDownLatch(1);
        final Reference<Boolean> done = new Reference<Boolean>(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    all.get(1, TimeUnit.SECONDS);
                    done.set(true);
                } catch (final Exception e) {
                    // Swallow
                } finally {
                    done.set(true);
                }
            }
        }).start();
        latch.await();
        Thread.sleep(100);

        p1.reject(new Exception());

        Assert.assertTrue(p1.isDone());
        Assert.assertFalse(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertFalse(done.get());

        p2.reject(new Exception());

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertFalse(done.get());

        p3.reject(new Exception());

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertTrue(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertFalse(done.get());

        p4.reject(new Exception());

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertTrue(p3.isDone());
        Assert.assertTrue(p4.isDone());
        Assert.assertTrue(all.isDone());
        Assert.assertFalse(done.get());

        final RejectedChain<Throwable> after = new RejectedChain<Throwable>(new Exception());
        p1.link(after);
        p2.link(after);
        p3.link(after);
        p4.link(after);

        Thread.sleep(100);
        Assert.assertTrue(done.get());
    }

    @Test
    public void cancelTest() throws Exception {
        final ChainImpl<Void> p1 = new ChainImpl<Void>();
        final ChainImpl<Void> p2 = new ChainImpl<Void>();
        final ChainImpl<Void> p3 = new ChainImpl<Void>();
        final ChainImpl<Void> p4 = new ChainImpl<Void>();
        final AllChain<Promise<?,?,?>> all = new AllChain<Promise<?,?,?>>(p1, p2, p3, p4);

        Assert.assertFalse(p1.isDone());
        Assert.assertFalse(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());

        final CountDownLatch latch = new CountDownLatch(1);
        final Reference<Boolean> done = new Reference<Boolean>(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    all.get(1, TimeUnit.SECONDS);
                    done.set(true);
                } catch (final Exception e) {
                    // Swallow
                } finally {
                    done.set(true);
                }
            }
        }).start();
        latch.await();
        Thread.sleep(100);

        p1.cancel(true);

        Assert.assertTrue(p1.isDone());
        Assert.assertFalse(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertFalse(done.get());

        p2.cancel(true);

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertFalse(done.get());

        p3.cancel(true);

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertTrue(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertFalse(done.get());

        p4.cancel(true);

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertTrue(p3.isDone());
        Assert.assertTrue(p4.isDone());
        Assert.assertTrue(all.isDone());
        Assert.assertFalse(done.get());

        final ResolvedChain<Void> after = new ResolvedChain<Void>(null);
        p1.link(after);
        p2.link(after);
        p3.link(after);
        p4.link(after);

        Thread.sleep(100);
        Assert.assertTrue(done.get());
    }
}
