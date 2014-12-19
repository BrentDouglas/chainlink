package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.AbstractInvocable;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.InvocationObserver;
import com.tangosol.net.InvocationService;
import com.tangosol.net.Member;
import com.tangosol.util.UID;
import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.transport.core.BaseDistributedRegistry;
import io.machinecode.chainlink.transport.core.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.transport.core.DistributedWorker;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.then.api.Deferred;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CoherenceRegistry extends BaseDistributedRegistry<Member, CoherenceRegistry> {

    private static final Logger log = Logger.getLogger(CoherenceRegistry.class);

    final String invocationServiceName;
    final InvocationService executor;

    final Member local;
    protected volatile List<Member> remotes;

    public CoherenceRegistry(final RegistryConfiguration configuration, final String invocationServiceName) throws Exception {
        super(configuration);
        this.invocationServiceName = invocationServiceName;
        final Cluster cluster = CacheFactory.ensureCluster();
        this.executor = (InvocationService) CacheFactory.getService(invocationServiceName);
        this.executor.setUserContext(this);
        this.local = cluster.getLocalMember();
        this.remotes = _remoteMembers(cluster.getMemberSet());
        log.infof("CoherenceRegistry started on address: [%s]", this.local); //TODO Message
    }

    @Override
    public void close() throws Exception {
        log.infof("CoherenceRegistry is shutting down."); //TODO Message
        this.executor.shutdown();
        super.close();
    }

    @Override
    public ExecutableId generateExecutableId() {
        return new CoherenceUUIDId(local);
    }

    @Override
    public WorkerId generateWorkerId(final Worker worker) {
        if (worker instanceof Thread) {
            return new CoherenceWorkerId((Thread)worker, local);
        } else {
            return new CoherenceUUIDId(local);
        }
    }

    @Override
    public ExecutionRepositoryId generateExecutionRepositoryId() {
        return new CoherenceUUIDId(local);
    }

    @Override
    public Member getLocal() {
        return local;
    }

    @Override
    protected List<Member> getRemotes() {
        return this.remotes;
    }

    @Override
    protected DistributedWorker<Member, CoherenceRegistry> createDistributedWorker(final Member address, final WorkerId workerId) {
        return new CoherenceWorker(this, this.local, address, workerId);
    }

    @Override
    protected DistributedProxyExecutionRepository<Member, CoherenceRegistry> createDistributedExecutionRepository(final ExecutionRepositoryId id, final Member address) {
        return new CoherenceProxyExecutionRepository(this, id, address);
    }

    @Override
    public <T> void invoke(final Member address, final DistributedCommand<T, Member, CoherenceRegistry> command, final Deferred<T, Throwable,?> deferred) {
        try {
            log.tracef("Invoking %s on %s.", command, address);
            this.executor.execute(
                    new Invocation(command, this.local, invocationServiceName),
                    Collections.singleton(address),
                    new DeferredObserver<>(deferred)
            );
        } catch (Exception e) {
            deferred.reject(e);
        }
    }

    @Override
    public <T> void invoke(final Member address, final DistributedCommand<T, Member, CoherenceRegistry> command, final Deferred<T, Throwable,?> promise, final long timeout, final TimeUnit unit) {
        invoke(address, command, promise);
    }

    public static class DeferredObserver<T> implements InvocationObserver {
        final Deferred<T,Throwable,?> deferred;

        public DeferredObserver(final Deferred<T, Throwable, ?> deferred) {
            this.deferred = deferred;
        }

        @Override
        public void memberCompleted(final Member member, final Object o) {
            deferred.resolve((T) o);
        }

        @Override
        public void memberFailed(final Member member, final Throwable throwable) {
            deferred.reject(throwable);
        }

        @Override
        public void memberLeft(final Member member) {
            deferred.reject(new Exception()); //TODO Message
        }

        @Override
        public void invocationCompleted() {}

    }

    public static class Invocation extends AbstractInvocable {
        private static final long serialVersionUID = 1L;

        private final DistributedCommand<?, Member, CoherenceRegistry> command;
        private final UID uuid;
        private final String invocationServiceName;
        private transient CoherenceRegistry registry;
        private transient Member origin;

        public Invocation(final DistributedCommand<?, Member, CoherenceRegistry> command, final Member origin, final String invocationServiceName) {
            this.command = command;
            this.invocationServiceName = invocationServiceName;
            this.uuid = origin.getUid();
        }

        @Override
        public void run() {
            this.registry = (CoherenceRegistry) CacheFactory.getService(this.invocationServiceName).getUserContext();
            for (final Object that : CacheFactory.getCluster().getMemberSet()) {
                final Member member = (Member) that;
                if (member.getUid().equals(this.uuid)) {
                    this.origin = member;
                    break;
                }
            }
            if (this.origin == null) {
                throw new IllegalStateException(); //TODO Message
            }
            try {
                command.perform(this.registry, this.origin);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
