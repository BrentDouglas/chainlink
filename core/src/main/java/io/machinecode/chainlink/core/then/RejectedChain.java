package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.OnLink;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.RejectedPromise;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
    public Promise<Void> await() {
        return new RejectedPromise<Void>(this.failure);
    }
}
