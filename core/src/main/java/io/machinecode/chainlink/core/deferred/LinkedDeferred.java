package io.machinecode.chainlink.core.deferred;

import io.machinecode.chainlink.jsl.core.util.ImmutablePair;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.deferred.Listener;
import org.jboss.logging.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LinkedDeferred<T> extends BaseDeferred<T> {

    private static final Logger log = Logger.getLogger(LinkedDeferred.class);

    protected volatile Deferred<?> link;

    @Override
    public void onLink(final Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException(); //TODO Message
        }
        lock.lock();
        try {
            if (this.link != null) {
                try {
                    listener.run(this.link);
                } catch (final Throwable e) {
                    final RuntimeException exception = new RuntimeException(Messages.format("CHAINLINK-004005.deferred.get.exception"), e);
                    log().warnf(exception, Messages.format("CHAINLINK-004005.deferred.get.exception"));
                    throw exception;
                }
            } else {
                while (!listenerLock.compareAndSet(false, true)) {}
                try {
                    linkListeners.add(listener);
                } finally {
                    listenerLock.set(false);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void await(final Lock lock, final Condition condition) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException(Messages.format("CHAINLINK-004004.deferred.interrupted"));
        }
        lock.lock();
        try {
            while (link == null) {
                this.lock.lock();
                try {
                    this.waiting.add(ImmutablePair.of(lock, condition));
                } finally {
                    this.lock.unlock();
                }
                condition.await();
            }
            try {
                if (link != null) {
                    link.await(lock, condition);
                }
            } catch (final Throwable e) {
                final RuntimeException exception = new RuntimeException(Messages.format("CHAINLINK-004005.deferred.get.exception"), e);
                log().warnf(exception, Messages.format("CHAINLINK-004005.deferred.get.exception"));
                throw exception;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void await(final long timeout, final TimeUnit unit, final Lock lock, final Condition condition) throws InterruptedException, TimeoutException {
        if (Thread.interrupted()) {
            throw new InterruptedException(Messages.format("CHAINLINK-004004.deferred.interrupted"));
        }
        final long timeoutMillis = unit.toMillis(timeout);
        final long end = System.currentTimeMillis() + timeoutMillis;
        lock.lock();
        try {
            while (link == null) {
                final long nextTimeout = _tryTimeout(end);
                this.lock.lock();
                try {
                    this.waiting.add(ImmutablePair.of(lock, condition));
                } finally {
                    this.lock.unlock();
                }
                condition.await(nextTimeout, MILLISECONDS);
            }
            try {
                final long nextTimeout = _tryTimeout(end);
                if (link != null) {
                    link.await(nextTimeout, MILLISECONDS, lock, condition);
                }
            } catch (final Throwable e) {
                final RuntimeException exception = new RuntimeException(Messages.format("CHAINLINK-004005.deferred.get.exception"), e);
                log().warnf(exception, Messages.format("CHAINLINK-004005.deferred.get.exception"));
                throw exception;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void link(final Deferred<?> that) {
        if (that == null) {
            throw new IllegalArgumentException(); //TODO message
        }
        lock.lock();
        try {
            link = that;
            RuntimeException exception = null;
            while (!listenerLock.compareAndSet(false, true)) {}
            try {
                for (final Listener listener : linkListeners) {
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
                listenerLock.set(false);
            }
            if (exception != null) {
                log().warnf(exception, Messages.format("CHAINLINK-004005.deferred.get.exception"));
                throw exception;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
