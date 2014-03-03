package io.machinecode.nock.core.local;

import io.machinecode.nock.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.util.concurrent.TimeUnit;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class LocalTransactionManager implements TransactionManager, UserTransaction {

    private static final Logger log = Logger.getLogger(LocalTransactionManager.class);

    final ThreadLocal<Transaction> transaction = new ThreadLocal<Transaction>();
    private final ThreadLocal<Long> timeout = new ThreadLocal<Long>();
    private final long defaultTimeout;

    public LocalTransactionManager(final long duration, final TimeUnit unit) {
        this.defaultTimeout = unit.toMillis(duration);
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {
        final Transaction current = this.transaction.get();
        if (current != null) {
            throw new NotSupportedException(Messages.format("NOCK-008002.transaction.manager.multiple.transactions.not.supported", Thread.currentThread(), current));
        }
        this.transaction.set(new LocalTransaction(this));
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        try {
            final Transaction transaction = this.transaction.get();
            if (transaction == null) {
                throw new IllegalStateException(Messages.format("NOCK-008001.transaction.manager.no.transaction", Thread.currentThread()));
            }
            transaction.commit();
        } finally {
            this.transaction.set(null);
        }
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        try {
            final Transaction transaction = this.transaction.get();
            if (transaction == null) {
                throw new IllegalStateException(Messages.format("NOCK-008001.transaction.manager.no.transaction", Thread.currentThread()));
            }
            transaction.rollback();
        } finally {
            this.transaction.set(null);
        }
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        final Transaction transaction = this.transaction.get();
        if (transaction == null) {
            throw new IllegalStateException(Messages.format("NOCK-008001.transaction.manager.no.transaction", Thread.currentThread()));
        }
        transaction.setRollbackOnly();
    }

    @Override
    public int getStatus() throws SystemException {
        final Transaction transaction = this.transaction.get();
        return transaction == null
                ? Status.STATUS_NO_TRANSACTION
                : transaction.getStatus();
    }

    @Override
    public Transaction getTransaction() throws SystemException {
        return this.transaction.get();
    }

    @Override
    public void setTransactionTimeout(final int seconds) throws SystemException {
        this.timeout.set(seconds == 0 ? defaultTimeout : TimeUnit.SECONDS.toMillis(seconds));
    }

    @Override
    public Transaction suspend() throws SystemException {
        final Transaction transaction = this.transaction.get();
        this.transaction.set(null);
        return transaction;
    }

    long timeout() {
        final Long that = this.timeout.get();
        return that == null ? defaultTimeout : that;
    }

    @Override
    public void resume(final Transaction transaction) throws InvalidTransactionException, IllegalStateException, SystemException {
        final Transaction current = this.transaction.get();
        if (current != null) {
            throw new IllegalStateException(Messages.format("NOCK-008000.transaction.manager.existing.transaction", Thread.currentThread(), current));
        }
        this.transaction.set(transaction);
    }
}
