package io.machinecode.chainlink.core.deferred;

import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.deferred.Listener;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AllDeferred<T> extends BaseDeferred<T> {

    private static final Logger log = Logger.getLogger(AllDeferred.class);

    protected final Deferred<?>[] link;

    public AllDeferred(final Deferred<?>... link) {
        this.link = link;
        final Listener signal = new Listener() {
            @Override
            public void run(final Deferred<?> that) {
                signal();
            }
        };
        for (final Deferred<?> that : link) {
            that.always(signal);
        }
        resolve(null);
    }

    @Override
    public boolean isDone() {
        boolean done = true;
        for (final Deferred<?> that : link) {
            if (that == null) {
                continue;
            }
            done = that.isDone() && done;
        }
        return done;
    }

    @Override
    public void onLink(final Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException(); //TODO Message
        }
        RuntimeException exception = null;
        lock.lock();
        try {
            for (final Deferred<?> that : link) {
                try {
                    listener.run(that);
                } catch (final Throwable e) {
                    if (exception == null) {
                        exception = new RuntimeException(Messages.format("CHAINLINK-004005.deferred.get.exception"), e);
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
        if (exception != null) {
            log().warnf(exception, Messages.format("CHAINLINK-004005.deferred.get.exception"));
            throw exception;
        }
    }

    @Override
    public void await(final Lock lock, final Condition condition) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException(Messages.format("CHAINLINK-004004.deferred.interrupted"));
        }
        RuntimeException exception = null;
        if (link != null) {
            for (final Deferred<?> that : link) {
                try {
                    that.await(lock, condition);
                } catch (final Throwable e) {
                    if (exception == null) {
                        exception = new RuntimeException(Messages.format("CHAINLINK-004005.deferred.get.exception"), e);
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
        }
        if (exception != null) {
            log().warnf(exception, Messages.format("CHAINLINK-004005.deferred.get.exception"));
            throw exception;
        }
    }

    @Override
    public void await(final long timeout, final TimeUnit unit, final Lock lock, final Condition condition) throws InterruptedException, TimeoutException {
        if (Thread.interrupted()) {
            throw new InterruptedException(Messages.format("CHAINLINK-004004.deferred.interrupted"));
        }
        final long timeoutMillis = unit.toMillis(timeout);
        final long end = System.currentTimeMillis() + timeoutMillis;
        RuntimeException exception = null;
        if (link != null) {
            for (final Deferred<?> that : link) {
                try {
                    final long nextTimeout = _tryTimeout(end);
                    that.await(nextTimeout, MILLISECONDS, lock, condition);
                } catch (final Throwable e) {
                    if (exception == null) {
                        exception = new RuntimeException(Messages.format("CHAINLINK-004005.deferred.get.exception"), e);
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
        }
        if (exception != null) {
            log().warnf(exception, Messages.format("CHAINLINK-004005.deferred.get.exception"));
            throw exception;
        }
    }

    @Override
    public void link(final Deferred<?> that) {
        // These should always be provided when being constructed
        throw new IllegalStateException(); //TODO Message
    }

    @Override
    protected Logger log() {
        return log;
    }
}
