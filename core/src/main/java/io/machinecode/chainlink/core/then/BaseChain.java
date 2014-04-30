package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.OnLink;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.then.api.OnCancel;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.core.DeferredImpl;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class BaseChain<T> extends DeferredImpl<T> implements Chain<T> {

    private static final Logger log = Logger.getLogger(BaseChain.class);

    protected volatile Chain<?> previous;
    protected final List<OnLink> onLinks = new LinkedList<OnLink>();

    @Override
    public Chain<T> previous(final Chain<?> that) {
        synchronized (this) {
            this.previous = that;
        }
        return this;
    }

    @Override
    public void notifyLinked() {
        final boolean set;
        synchronized (this) {
            notifyAll();
            set = previous != null;
        }
        if (set) {
            previous.notifyLinked();
        }
    }

    @Override
    protected void doCancel(final boolean futureCompatible) {
        log().tracef(getCancelLogMessage());
        final CancelListener listener = new CancelListener();
        RuntimeException exception = null;
        _lock();
        try {
            cancelling(listener);
            if (listener.exception != null) {
                _signalAll();
                throw listener.exception;
            }
            if (this.checkCancelled(futureCompatible)) {
                return;
            }
            this.state = CANCELLED;
        } finally {
            _unlock();
        }
        _in();
        try {
            for (final OnCancel then : onCancels) {
                try {
                    then.cancel();
                } catch (final Throwable e) {
                    if (exception == null) {
                        exception = new RuntimeException(Messages.format("CHAINLINK-004006.chain.cancel.exception"), e);
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
            for (final OnComplete on : onCompletes) {
                try {
                    on.complete();
                } catch (final Throwable e) {
                    if (exception == null) {
                        exception = new RuntimeException(Messages.format("CHAINLINK-004006.chain.cancel.exception"), e);
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
        } finally {
            _out();
            _lock();
            try {
                _signalAll();
            } finally {
                _unlock();
            }
        }
    }

    protected void cancelling(final OnLink on) {
        onLink(on);
    }

    @Override
    public T get() throws InterruptedException, ExecutionException, CancellationException {
        if (Thread.interrupted()) {
            throw new InterruptedException(getInterruptedExceptionMessage());
        }
        await().get();
        _lock();
        try {
            for (;;) {
                switch (this.state) {
                    case CANCELLED:
                        throw new CancellationException(Messages.format("CHAINLINK-004002.chain.cancelled"));
                    case REJECTED:
                        throw new ExecutionException(Messages.format("CHAINLINK-004001.chain.rejected"), failure);
                    case RESOLVED:
                        return value;
                    //default/PENDING means this thread was notified before the computation actually completed
                }
                _await();
            }
        } finally {
            _unlock();
        }
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException, CancellationException {
        if (Thread.interrupted()) {
            throw new InterruptedException(getInterruptedExceptionMessage());
        }
        final long end = System.currentTimeMillis() + unit.toMillis(timeout);
        long nextTimeout = _tryTimeout(end);
        await().get(nextTimeout, MILLISECONDS);
        _lock();
        try {
            for (;;) {
                switch (this.state) {
                    case CANCELLED:
                        throw new CancellationException(Messages.format("CHAINLINK-004002.chain.cancelled"));
                    case REJECTED:
                        throw new ExecutionException(Messages.format("CHAINLINK-004001.chain.rejected"), failure);
                    case RESOLVED:
                        return value;
                }
                nextTimeout = _tryTimeout(end);
                _await(nextTimeout, MILLISECONDS);
            }
        } finally {
            _unlock();
        }
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected String getResolveLogMessage() {
        return Messages.get("CHAINLINK-004100.chain.resolve");
    }

    @Override
    protected String getRejectLogMessage() {
        return Messages.get("CHAINLINK-004101.chain.reject");
    }

    @Override
    protected String getCancelLogMessage() {
        return Messages.get("CHAINLINK-004102.chain.cancel");
    }

    @Override
    protected String getTimeoutExceptionMessage() {
        return Messages.get("CHAINLINK-004003.chain.timeout");
    }

    @Override
    protected String getInterruptedExceptionMessage() {
        return Messages.get("CHAINLINK-004004.chain.interrupted");
    }

    private static final class CancelListener implements OnLink, Serializable {
        private RuntimeException exception = null;

        @Override
        public void link(final Chain<?> that) {
            try {
                that.cancel();
            } catch (final Throwable e) {
                if (exception == null) {
                    exception = new RuntimeException(Messages.format("CHAINLINK-004006.chain.cancel.exception"), e);
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
    }
}
