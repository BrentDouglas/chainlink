package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.core.deferred.AllDeferredImpl;
import io.machinecode.chainlink.core.deferred.DeferredImpl;
import io.machinecode.chainlink.spi.deferred.Deferred;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DeferredTest {

    private static class Def extends DeferredImpl<Deferred<?>> {

        private Def(final Deferred<?>... chain) {
            super(chain);
        }

        public void execute(final Deferred<?> that) {
            try {
                resolve(setChild(0, that));
            } finally {
                _notifyAll();
            }
        }
    }

    @Test
    public void basicDeferredTest() throws Exception {
        final Def after = new Def();

        final Def p1 = new Def(new Deferred[1]);
        final Def p2 = new Def(new Deferred[1]);
        final Def p3 = new Def(after);
        final Def p4 = new Def(after);

        final AllDeferredImpl<Deferred<?>> before = new AllDeferredImpl<Deferred<?>>(p1, p2, p3, p4);

        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                before.cancel(true);
            }
        }).start();

        latch.await();
        Thread.sleep(100);
        p1.execute(after);
        p2.execute(after);

        before.cancel(true);

        before.get(100, TimeUnit.MILLISECONDS);
    }
}
