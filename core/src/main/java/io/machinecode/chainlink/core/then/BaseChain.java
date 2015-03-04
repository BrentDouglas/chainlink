package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.OnLink;
import io.machinecode.then.api.ListenerException;
import io.machinecode.then.api.OnCancel;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.core.DeferredImpl;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class BaseChain<T> extends DeferredImpl<T,Throwable,Void> implements Chain<T> {

    private static final Logger log = Logger.getLogger(BaseChain.class);

    protected static final byte ON_LINK = 106;

    protected final Object _linkLock = new Object();
    protected volatile Chain<?> previous;

    @Override
    public void linkAndResolve(final T value, final Chain<?> link) {
        this.link(link);
        this.resolve(value);
    }

    @Override
    public void linkAndReject(final Throwable failure, final Chain<?> link) {
        this.link(link);
        this.reject(failure);
    }

    @Override
    public void previous(final Chain<?> that) {
        synchronized (_linkLock) {
            this.previous = that;
        }
    }

    @Override
    public void notifyLinked() {
        final boolean set;
        synchronized (_linkLock) {
            _linkLock.notifyAll();
            set = this.previous != null;
        }
        if (set) {
            this.previous.notifyLinked();
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
        final Iterable<OnCancel> onCancels;
        final Iterable<OnComplete> onCompletes;
        final int state;
        synchronized (lock) {
            cancelling(listener);
            if (listener.exception != null) {
                lock.notifyAll();
                throw listener.exception;
            }
            if (setCancelled()) {
                return isCancelled();
            }
            state = this.state;
            onCancels = this.getEvents(ON_CANCEL);
            onCompletes = this.getEvents(ON_COMPLETE);
        }
        for (final OnCancel on : onCancels) {
            try {
                on.cancel(interrupt);
            } catch (final Throwable e) {
                if (exception == null) {
                    exception = new ListenerException(io.machinecode.then.core.Messages.format("THEN-000302.promise.on.cancel.exception"), e);
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        for (final OnComplete on : onCompletes) {
            try {
                on.complete(state);
            } catch (final Throwable e) {
                if (exception == null) {
                    exception = new ListenerException(io.machinecode.then.core.Messages.format("THEN-000303.promise.on.complete.exception"), e);
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        synchronized (lock) {
            lock.notifyAll();
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
        try {
            awaitLink();
        } catch (final CancellationException | ExecutionException e) {
            try {
                super.get();
            } catch (final Exception n) {
                e.addSuppressed(n);
            }
            throw e;
        }
        return super.get();
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (Thread.interrupted()) {
            throw new InterruptedException(getInterruptedExceptionMessage());
        }
        final long end = System.currentTimeMillis() + unit.toMillis(timeout);
        try {
            awaitLink(_tryTimeout(end), MILLISECONDS);
        } catch (final CancellationException | ExecutionException e) {
            try {
                super.get(_tryTimeout(end), MILLISECONDS);
            } catch (final Exception n) {
                e.addSuppressed(n);
            }
            throw e;
        }
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
        return Messages.get("CHAINLINK-004000.chain.timeout");
    }

    @Override
    protected String getInterruptedExceptionMessage() {
        return Messages.get("CHAINLINK-004001.chain.interrupted");
    }

    private static final class CancelListener implements OnLink, Serializable {
        private static final long serialVersionUID = 1L;

        private RuntimeException exception = null;

        @Override
        public void link(final Chain<?> that) {
            try {
                that.cancel(true);
            } catch (final Throwable e) {
                if (exception == null) {
                    exception = new RuntimeException(Messages.format("CHAINLINK-004003.chain.cancel.exception"), e);
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
    }
}
