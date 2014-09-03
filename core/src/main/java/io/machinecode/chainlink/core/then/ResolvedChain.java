package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.OnLink;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.ResolvedPromise;

import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ResolvedChain<T> extends BaseChain<T> {

    public ResolvedChain(final T value) {
        resolve(value);
        notifyLinked();
    }

    @Override
    public ResolvedChain<T> link(final Chain<?> that) {
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
