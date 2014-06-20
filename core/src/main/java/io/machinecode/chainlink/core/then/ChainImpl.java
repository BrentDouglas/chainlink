package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.OnLink;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.PromiseImpl;
import org.jboss.logging.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChainImpl<T> extends BaseChain<T> {

    private static final Logger log = Logger.getLogger(ChainImpl.class);

    protected volatile Chain<?> link;
    protected final AwaitPromise promise = new AwaitPromise();

    public ChainImpl() {
        this.onLink(promise);
    }

    @Override
    public ChainImpl<T> link(final Chain<?> that) {
        if (that == null) {
            throw new IllegalArgumentException(Messages.format("CHAINLINK-004200.chain.argument.required", "link"));
        }
        synchronized (this) {
            this.link = that;
            that.previous(this);
        }
        RuntimeException exception = null;
        for (final OnLink on : this.<OnLink>_getEvents(ON_LINK)) {
            try {
                on.link(that);
            } catch (final Throwable e) {
                if (exception == null) {
                    exception = new RuntimeException(Messages.format("CHAINLINK-004005.chain.get.exception"), e);
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        notifyLinked();
        if (exception != null) {
            log().warnf(exception, Messages.format("CHAINLINK-004005.chain.get.exception"));
            throw exception;
        }
        return this;
    }

    @Override
    public ChainImpl<T> onLink(final OnLink then) {
        if (then == null) {
            throw new IllegalArgumentException(Messages.format("CHAINLINK-004200.chain.argument.required", "onLink"));
        }
        synchronized (this) {
            if (this.link != null) {
                try {
                    then.link(this.link);
                } catch (final Throwable e) {
                    final RuntimeException exception = new RuntimeException(Messages.format("CHAINLINK-004200.chain.link.exception"), e);
                    log().warnf(exception, Messages.format("CHAINLINK-004200.chain.link.exception"));
                    throw exception;
                }
            } else {
                _addEvent(ON_LINK, then);
            }
        }
        return this;
    }

    @Override
    public Promise<Void> awaitLink() {
        return promise;
    }

    @Override
    protected Logger log() {
        return log;
    }

    public class AwaitPromise extends PromiseImpl<Void> implements OnLink {

        @Override
        public Void get() throws InterruptedException, ExecutionException {
            synchronized (ChainImpl.this) {
                while (ChainImpl.this.link == null) {
                    ChainImpl.this.wait();
                }
            }
            return super.get();
        }

        @Override
        public Void get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            final long end = System.currentTimeMillis() + unit.toMillis(timeout);
            synchronized (ChainImpl.this) {
                while (ChainImpl.this.link == null) {
                    ChainImpl.this.wait(unit.toMillis(timeout));
                }
            }
            return super.get(_tryTimeout(end), MILLISECONDS);
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
