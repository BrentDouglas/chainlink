package io.machinecode.chainlink.test.core;

import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;
import io.machinecode.chainlink.core.then.AllChain;
import io.machinecode.chainlink.core.then.ChainImpl;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChainTest {

    private static class Def extends ChainImpl<Chain<?>> {

        public void execute(final Chain<?> that) {
            try {
                link(that);
                resolve(that);
            } finally {
                _signalAll();
            }
        }
    }

    @Test
    @Ignore
    public void basicChainTest() throws Exception {
        final Def after = new Def();

        final Def p1 = new Def();
        final Def p2 = new Def();
        final Def p3 = new Def();
        final Def p4 = new Def();

        final AllChain<Promise<?>> all = new AllChain<Promise<?>>(p1, p2, p3, p4);

        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                all.cancel(true);
            }
        }).start();

        latch.await();
        Thread.sleep(100);
        p1.execute(after);
        p2.execute(after);

        all.cancel(true);

        all.get(100, TimeUnit.MILLISECONDS);
    }
}
