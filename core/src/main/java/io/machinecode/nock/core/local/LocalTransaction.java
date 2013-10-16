package io.machinecode.nock.core.local;

import gnu.trove.set.hash.THashSet;
import org.jboss.logging.Logger;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class LocalTransaction implements UserTransaction, Transaction {

    private static final Logger log = Logger.getLogger(LocalTransaction.class);

    private final LocalTransactionManager manager;

    private long timeout;
    private long start;
    private int status = Status.STATUS_ACTIVE;

    private Set<Synchronization> syncs;

    public LocalTransaction(final LocalTransactionManager manager) {
        this.manager = manager;
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {
        this.start = System.currentTimeMillis();
        this.timeout = this.manager.timeout();
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        try {
            final long end = System.currentTimeMillis();
            _before();
            switch (this.status) {
                case Status.STATUS_ACTIVE:
                    break;
                default:
                    //Rollback
                    throw new RollbackException();
            }
            if (end - this.start > this.timeout) {
                //Rollback
                throw new RollbackException();
            }
            //this.status = Status.STATUS_COMMITTING;
            //XA
            this.status = Status.STATUS_COMMITTED;
            _after(Status.STATUS_COMMITTED);
        } finally {
            this.manager.transaction.set(null);
        }
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        try {
            switch (this.status) {
                case Status.STATUS_COMMITTED:
                case Status.STATUS_PREPARED:
                    throw new IllegalStateException();
            }
            this.status = Status.STATUS_ROLLEDBACK;
            _after(Status.STATUS_ROLLEDBACK);
        } finally {
            this.manager.transaction.set(null);
        }
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        if (this.status != Status.STATUS_ACTIVE) {
            throw new IllegalStateException();
        }
        this.status = Status.STATUS_MARKED_ROLLBACK;
    }

    @Override
    public int getStatus() throws SystemException {
        return this.status;
    }

    @Override
    public void setTransactionTimeout(final int seconds) throws SystemException {
        this.manager.setTransactionTimeout(seconds);
    }

    // XA

    @Override
    public boolean enlistResource(final XAResource xaRes) throws RollbackException, IllegalStateException, SystemException {
        return false;
    }

    @Override
    public boolean delistResource(final XAResource xaRes, final int flag) throws IllegalStateException, SystemException {
        return false;
    }

    @Override
    public void registerSynchronization(final Synchronization sync) throws RollbackException, IllegalStateException, SystemException {
        if (this.syncs == null) {
            this.syncs = new THashSet<Synchronization>(1);
        }
        switch (this.status) {
            case Status.STATUS_ACTIVE:
            case Status.STATUS_PREPARING:
                this.syncs.add(sync);
                return;
            case Status.STATUS_MARKED_ROLLBACK:
            case Status.STATUS_ROLLING_BACK:
            case Status.STATUS_ROLLEDBACK:
                throw new RollbackException();
            default:
                throw new IllegalStateException();
        }
    }

    private void _before() {
        for (final Synchronization sync : this.syncs) {
            try {
                sync.beforeCompletion();
            } catch (final Exception e) {
                this.status = Status.STATUS_MARKED_ROLLBACK;
            }
        }
    }

    private void _after(final int status) {
        for (final Synchronization sync : this.syncs) {
            try {
                sync.afterCompletion(status);
            } catch (final Exception e) {
                // TODO log.error();
            }
        }
        this.syncs.clear();
    }
}
