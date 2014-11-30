package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.OnLink;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class RejectedChain<T> extends BaseChain<T> {

    public RejectedChain(final Throwable value) {
        reject(value);
        notifyLinked();
    }

    @Override
    public RejectedChain<T> link(final Chain<?> that) {
        throw new IllegalStateException(); //TODO Message This is a terminal link
    }

    @Override
    public RejectedChain<T> onLink(final OnLink then) {
        // noop
        return this;
    }

    @Override
    public void awaitLink() {
        // no op
    }

    @Override
    public void awaitLink(final long timeout, final TimeUnit unit) throws InterruptedException {
        // no op
    }
}
