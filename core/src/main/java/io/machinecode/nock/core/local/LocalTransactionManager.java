package io.machinecode.nock.core.local;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class LocalTransactionManager implements TransactionManager {

    private final ThreadLocal<Transaction> transaction = new ThreadLocal<Transaction>();
    volatile long timeout;

    @Override
    public void begin() throws NotSupportedException, SystemException {
        final Transaction current = this.transaction.get();
        if (current != null) {
            throw new NotSupportedException();
        }
        this.transaction.set(new LocalTransaction(this));
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        final Transaction transaction = this.transaction.get();
        if (transaction == null) {
            throw new IllegalStateException();
        }
        transaction.commit();
        this.transaction.set(null);
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        final Transaction transaction = this.transaction.get();
        if (transaction == null) {
            throw new IllegalStateException();
        }
        transaction.rollback();
        this.transaction.set(null);
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        final Transaction transaction = this.transaction.get();
        if (transaction == null) {
            throw new IllegalStateException();
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
        this.timeout = seconds * 1000;
    }

    @Override
    public Transaction suspend() throws SystemException {
        final Transaction transaction = this.transaction.get();
        this.transaction.set(null);
        return transaction;
    }

    @Override
    public void resume(final Transaction transaction) throws InvalidTransactionException, IllegalStateException, SystemException {
        final Transaction current = this.transaction.get();
        if (current != null) {
            throw new IllegalStateException();
        }
        this.transaction.set(transaction);
    }
}
