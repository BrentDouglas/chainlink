package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.core.then.AllChain;
import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.then.RejectedChain;
import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.then.api.Promise;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class AllChainTest {

    @Test
    public void resolveTest() throws Exception {
        final ChainImpl<Void> p1 = new ChainImpl<>();
        final ChainImpl<Void> p2 = new ChainImpl<>();
        final ChainImpl<Void> p3 = new ChainImpl<>();
        final ChainImpl<Void> p4 = new ChainImpl<>();
        final AllChain<Promise<?,?,?>> all = new AllChain<>(p1, p2, p3, p4);

        Assert.assertFalse(p1.isDone());
        Assert.assertFalse(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());

        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    all.get(1, TimeUnit.SECONDS);
                } catch (final Exception e) {
                    // Swallow
                } finally {
                    done.countDown();
                }
            }
        }).start();
        latch.await();

        p1.resolve(null);

        Assert.assertTrue(p1.isDone());
        Assert.assertFalse(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertEquals(1, done.getCount());

        p2.resolve(null);

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertEquals(1, done.getCount());

        p3.resolve(null);

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertTrue(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertEquals(1, done.getCount());

        p4.resolve(null);

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertTrue(p3.isDone());
        Assert.assertTrue(p4.isDone());
        Assert.assertTrue(all.isDone());
        Assert.assertEquals(1, done.getCount());

        final ResolvedChain<Void> after = new ResolvedChain<>(null);
        p1.link(after);
        p2.link(after);
        p3.link(after);
        p4.link(after);

        done.await(2, TimeUnit.SECONDS);
        Assert.assertEquals(0, done.getCount());
    }

    @Test
    public void rejectTest() throws Exception {
        final ChainImpl<Void> p1 = new ChainImpl<>();
        final ChainImpl<Void> p2 = new ChainImpl<>();
        final ChainImpl<Void> p3 = new ChainImpl<>();
        final ChainImpl<Void> p4 = new ChainImpl<>();
        final AllChain<Promise<?,?,?>> all = new AllChain<>(p1, p2, p3, p4);

        Assert.assertFalse(p1.isDone());
        Assert.assertFalse(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());

        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    all.get(1, TimeUnit.SECONDS);
                } catch (final Exception e) {
                    // Swallow
                } finally {
                    done.countDown();
                }
            }
        }).start();
        latch.await();

        p1.reject(new Exception());

        Assert.assertTrue(p1.isDone());
        Assert.assertFalse(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertEquals(1, done.getCount());

        p2.reject(new Exception());

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertEquals(1, done.getCount());

        p3.reject(new Exception());

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertTrue(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertEquals(1, done.getCount());

        p4.reject(new Exception());

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertTrue(p3.isDone());
        Assert.assertTrue(p4.isDone());
        Assert.assertTrue(all.isDone());
        Assert.assertEquals(1, done.getCount());

        final RejectedChain<Throwable> after = new RejectedChain<Throwable>(new Exception());
        p1.link(after);
        p2.link(after);
        p3.link(after);
        p4.link(after);

        done.await(2, TimeUnit.SECONDS);
        Assert.assertEquals(0, done.getCount());
    }

    @Test
    public void cancelTest() throws Exception {
        final ChainImpl<Void> p1 = new ChainImpl<>();
        final ChainImpl<Void> p2 = new ChainImpl<>();
        final ChainImpl<Void> p3 = new ChainImpl<>();
        final ChainImpl<Void> p4 = new ChainImpl<>();
        final AllChain<Promise<?,?,?>> all = new AllChain<>(p1, p2, p3, p4);

        Assert.assertFalse(p1.isDone());
        Assert.assertFalse(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());

        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    all.get(1, TimeUnit.SECONDS);
                } catch (final Exception e) {
                    // Swallow
                } finally {
                    done.countDown();
                }
            }
        }).start();
        latch.await();

        p1.cancel(true);

        Assert.assertTrue(p1.isDone());
        Assert.assertFalse(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertEquals(1, done.getCount());

        p2.cancel(true);

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertFalse(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertEquals(1, done.getCount());

        p3.cancel(true);

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertTrue(p3.isDone());
        Assert.assertFalse(p4.isDone());
        Assert.assertFalse(all.isDone());
        Assert.assertEquals(1, done.getCount());

        p4.cancel(true);

        Assert.assertTrue(p1.isDone());
        Assert.assertTrue(p2.isDone());
        Assert.assertTrue(p3.isDone());
        Assert.assertTrue(p4.isDone());
        Assert.assertTrue(all.isDone());
        Assert.assertEquals(1, done.getCount());

        final ResolvedChain<Void> after = new ResolvedChain<>(null);
        p1.link(after);
        p2.link(after);
        p3.link(after);
        p4.link(after);

        done.await(2, TimeUnit.SECONDS);
        Assert.assertEquals(0, done.getCount());
    }
}
