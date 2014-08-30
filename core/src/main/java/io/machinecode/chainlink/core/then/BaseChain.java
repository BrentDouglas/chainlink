package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.OnLink;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.then.api.ListenerException;
import io.machinecode.then.api.OnCancel;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.core.PromiseImpl;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class BaseChain<T> extends PromiseImpl<T,Throwable> implements Chain<T> {

    private static final Logger log = Logger.getLogger(BaseChain.class);

    protected static final byte ON_LINK = 106;

    protected volatile Chain<?> previous;

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
    protected boolean setCancelled() {
        switch (this.state) {
            case CANCELLED:
                return true;
            case REJECTED:
            case RESOLVED:
                return false;
        }
        this.state = CANCELLED;
        return false;
    }

    @Override
    public boolean cancel(final boolean interrupt) {
        log().tracef(getCancelLogMessage());
        final CancelListener listener = new CancelListener();
        ListenerException exception = null;
        _lock();
        try {
            cancelling(listener);
            if (listener.exception != null) {
                _signalAll();
                throw listener.exception;
            }
            if (setCancelled()) {
                return isCancelled();
            }
        } finally {
            _unlock();
        }
        for (final OnCancel then : this.<OnCancel>_getEvents(ON_CANCEL)) {
            try {
                then.cancel(interrupt);
            } catch (final Throwable e) {
                if (exception == null) {
                    exception = new ListenerException(io.machinecode.then.core.Messages.format("THEN-000013.promise.cancel.exception"), e);
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        for (final OnComplete on : this.<OnComplete>_getEvents(ON_COMPLETE)) {
            try {
                on.complete();
            } catch (final Throwable e) {
                if (exception == null) {
                    exception = new ListenerException(io.machinecode.then.core.Messages.format("THEN-000013.promise.cancel.exception"), e);
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        _lock();
        try {
            _signalAll();
        } finally {
            _unlock();
        }
        if (exception != null) {
            throw exception;
        }
        return true;
    }

    protected void cancelling(final OnLink on) {
        onLink(on);
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        if (Thread.interrupted()) {
            throw new InterruptedException(getInterruptedExceptionMessage());
        }
        awaitLink().get();
        return super.get();
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (Thread.interrupted()) {
            throw new InterruptedException(getInterruptedExceptionMessage());
        }
        final long end = System.currentTimeMillis() + unit.toMillis(timeout);
        awaitLink().get(_tryTimeout(end), MILLISECONDS);
        return super.get(_tryTimeout(end), MILLISECONDS);
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
                that.cancel(true);
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
