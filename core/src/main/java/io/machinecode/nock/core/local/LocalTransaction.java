package io.machinecode.nock.core.local;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class LocalTransaction implements UserTransaction, Transaction {

    private final LocalTransactionManager manager;

    private volatile long timeout;
    private volatile long start;
    private volatile int status = Status.STATUS_ACTIVE;

    public LocalTransaction(final LocalTransactionManager manager) {
        this.manager = manager;
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {
        start = System.nanoTime();
        timeout = manager.timeout;
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        final long end = System.nanoTime();
        if (this.status != Status.STATUS_ACTIVE) {
            throw new RollbackException();
        }
        this.status = Status.STATUS_COMMITTING;
        if (end - start < timeout) {
            throw new RollbackException();
        }
        this.status = Status.STATUS_COMMITTED;
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        this.status = Status.STATUS_ROLLEDBACK;
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        this.status = Status.STATUS_MARKED_ROLLBACK;
    }

    @Override
    public int getStatus() throws SystemException {
        return status;
    }

    @Override
    public void setTransactionTimeout(final int seconds) throws SystemException {
        this.manager.setTransactionTimeout(seconds);
    }

    // XA

    @Override
    public boolean enlistResource(final XAResource xaRes) throws RollbackException, IllegalStateException, SystemException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean delistResource(final XAResource xaRes, final int flag) throws IllegalStateException, SystemException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void registerSynchronization(final Synchronization sync) throws RollbackException, IllegalStateException, SystemException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
