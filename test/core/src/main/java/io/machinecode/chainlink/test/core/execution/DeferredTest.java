package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.core.deferred.AllDeferred;
import io.machinecode.chainlink.core.deferred.LinkedDeferred;
import io.machinecode.chainlink.spi.deferred.Deferred;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DeferredTest {

    private static class Def extends LinkedDeferred<Deferred<?>> {

        public void execute(final Deferred<?> that) {
            try {
                link(that);
                resolve(that);
            } finally {
                signal();
            }
        }
    }

    @Test
    @Ignore
    public void basicDeferredTest() throws Exception {
        final Def after = new Def();

        final Def p1 = new Def();
        final Def p2 = new Def();
        final Def p3 = new Def();
        final Def p4 = new Def();

        final AllDeferred<Deferred<?>> before = new AllDeferred<Deferred<?>>(p1, p2, p3, p4);

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
