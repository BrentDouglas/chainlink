package io.machinecode.nock.core.local;

import gnu.trove.set.hash.THashSet;
import io.machinecode.nock.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;
import java.util.Set;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class LocalTransaction implements Transaction {

    private static final Logger log = Logger.getLogger(LocalTransaction.class);

    private final LocalTransactionManager manager;

    private final long timeout;
    private int status = Status.STATUS_ACTIVE;

    private Set<Synchronization> syncs;

    public LocalTransaction(final LocalTransactionManager manager) {
        this.manager = manager;
        this.timeout = System.currentTimeMillis() + this.manager.timeout();
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
                    throw new RollbackException(Messages.format("NOCK-007000.transaction.cant.commit", status, _status(Status.STATUS_ACTIVE)));
            }
            if (this.timeout < end) {
                throw new RollbackException(Messages.format("NOCK-007003.transaction.timed.out"));
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
                    throw new IllegalStateException(Messages.format("NOCK-007001.transaction.cant.rollback", _status(status)));
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
            throw new IllegalStateException(Messages.format("NOCK-007002.transaction.cant.set.rollback.only", _status(status), _status(Status.STATUS_MARKED_ROLLBACK), _status(Status.STATUS_ACTIVE)));
        }
        this.status = Status.STATUS_MARKED_ROLLBACK;
    }

    @Override
    public int getStatus() throws SystemException {
        return this.status;
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
                throw new RollbackException(Messages.format("NOCK-007004.transaction.rolling.back", _status(status)));
            default:
                throw new IllegalStateException(Messages.format("NOCK-007005.transaction.illegal.state", _status(status)));
        }
    }

    private void _before() {
        if (this.syncs == null) {
            return;
        }
        for (final Synchronization sync : this.syncs) {
            try {
                sync.beforeCompletion();
            } catch (final Exception e) {
                this.status = Status.STATUS_MARKED_ROLLBACK;
                log.tracef(e, Messages.get("NOCK-007100.transaction.before.completion.exception"));
            }
        }
    }

    private void _after(final int status) {
        if (this.syncs == null) {
            return;
        }
        for (final Synchronization sync : this.syncs) {
            try {
                sync.afterCompletion(status);
            } catch (final Exception e) {
                log.tracef(e, Messages.get("NOCK-007101.transaction.after.completion.exception"));
            }
        }
        this.syncs.clear();
    }

    @Override
    public String toString() {
        return "LocalTransaction[status=" + _status(status) + ",timeout=" + timeout + ",syncs=" + (syncs == null ? 0 : syncs.size()) + "]";
    }

    private static String _status(final int status) {
        switch (status) {
            case Status.STATUS_ACTIVE: return "STATUS_ACTIVE";
            case Status.STATUS_MARKED_ROLLBACK: return "STATUS_MARKED_ROLLBACK";
            case Status.STATUS_PREPARED: return "STATUS_PREPARED";
            case Status.STATUS_COMMITTED: return "STATUS_COMMITTED";
            case Status.STATUS_ROLLEDBACK: return "STATUS_ROLLEDBACK";
            case Status.STATUS_UNKNOWN: return "STATUS_UNKNOWN";
            case Status.STATUS_NO_TRANSACTION: return "STATUS_NO_TRANSACTION";
            case Status.STATUS_PREPARING: return "STATUS_PREPARING";
            case Status.STATUS_COMMITTING: return "STATUS_COMMITTING";
            case Status.STATUS_ROLLING_BACK: return "STATUS_ROLLING_BACK";
        }
        return Integer.toString(status);
    }
}
