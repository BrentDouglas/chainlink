package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.OnLink;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ResolvedChain<T> extends BaseChain<T> {

    public ResolvedChain(final T value) {
        resolve(value);
        notifyLinked();
    }

    @Override
    public void link(final Chain<?> that) {
        throw new IllegalStateException(); //TODO Message This is a terminal link
    }

    @Override
    public ResolvedChain<T> onLink(final OnLink then) {
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
