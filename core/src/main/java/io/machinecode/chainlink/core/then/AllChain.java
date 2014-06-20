package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.OnLink;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.PromiseImpl;
import org.jboss.logging.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AllChain<T> extends BaseChain<T> {

    private static final Logger log = Logger.getLogger(AllChain.class);

    protected final Chain<?>[] link;
    protected final AwaitPromise promise = new AwaitPromise();

    public AllChain(final Chain<?>... link) {
        this.link = link;
        for (final Chain<?> that : link) {
            that.previous(this);
            that.onLink(promise);
        }
        resolve(null);
    }

    @Override
    public boolean isDone() {
        boolean done = true;
        for (final Promise<?> that : link) {
            if (that == null) {
                continue;
            }
            done = that.isDone() && done;
        }
        return done;
    }

    @Override
    public AllChain<T> onLink(final OnLink then) {
        if (then == null) {
            throw new IllegalArgumentException(Messages.format("CHAINLINK-004200.chain.argument.required", "onLink"));
        }
        RuntimeException exception = null;
        for (final Chain<?> that : link) {
            try {
                then.link(that);
            } catch (final Throwable e) {
                if (exception == null) {
                    exception = new RuntimeException(Messages.format("CHAINLINK-004005.chain.get.exception"), e);
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        if (exception != null) {
            log().warnf(exception, Messages.format("CHAINLINK-004005.chain.get.exception"));
            throw exception;
        }
        return this;
    }

    @Override
    public Promise<Void> awaitLink() {
        return promise;
    }

    @Override
    public AllChain<T> link(final Chain<?> that) {
        // These should always be provided when being constructed
        throw new IllegalStateException(); //TODO Message
    }

    @Override
    protected Logger log() {
        return log;
    }

    protected class AwaitPromise extends PromiseImpl<Void> implements OnLink {
        final AtomicInteger count = new AtomicInteger();

        @Override
        public void resolve(final Void value) {
            if (count.incrementAndGet() < link.length) {
                return;
            }
            super.resolve(value);
        }

        @Override
        public void reject(final Throwable failure) {
            if (count.incrementAndGet() < link.length) {
                return;
            }
            super.reject(failure);
        }

        @Override
        public void link(final Chain<?> chain) {
            try {
                chain.awaitLink()
                        .onResolve(this)
                        .onReject(this);
            } catch (final Throwable e) {
                log().warnf(e, Messages.format("CHAINLINK-004005.chain.get.exception"));
                this.reject(e);
            }
        }
    }
}
