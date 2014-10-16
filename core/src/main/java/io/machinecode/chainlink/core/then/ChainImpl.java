package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.OnLink;
import io.machinecode.chainlink.spi.util.Messages;
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
        this.notifyLinked();
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
    public void awaitLink() throws InterruptedException, ExecutionException {
        synchronized (this) {
            while (this.link == null) {
                this.wait();
            }
        }
        try {
            this.link.get();
        } catch (final Exception e) {
            // Swallow
        }
    }

    @Override
    public void awaitLink(final long timeout, final TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
        final long end = System.currentTimeMillis() + unit.toMillis(timeout);
        synchronized (this) {
            while (this.link == null) {
                this.wait(_tryTimeout(end));
            }
        }
        try {
            this.link.get(_tryTimeout(end), MILLISECONDS);
        } catch (final Exception e) {
            // Swallow
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
