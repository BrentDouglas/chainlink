package io.machinecode.chainlink.transport.hazelcast;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.core.transport.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.core.transport.DistributedTransport;
import io.machinecode.chainlink.core.transport.DistributedWorker;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.core.FutureDeferred;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class HazelcastTransport extends DistributedTransport<Member> {

    private static final Logger log = Logger.getLogger(HazelcastTransport.class);

    final HazelcastInstance hazelcast;
    final IExecutorService executor;

    final Member local;
    protected volatile List<Member> remotes;

    public HazelcastTransport(final Dependencies dependencies, final Properties properties, final HazelcastInstance hazelcast, final IExecutorService executor) throws Exception {
        super(dependencies, properties);
        this.hazelcast = hazelcast;
        this.executor = executor;

        this.hazelcast.getUserContext().put(HazelcastTransport.class.getCanonicalName(), this);

        final Cluster cluster = hazelcast.getCluster();
        this.local = cluster.getLocalMember();
        this.remotes = _remoteMembers(cluster.getMembers());
        cluster.addMembershipListener(new MembershipListener() {
            @Override
            public void memberAdded(final MembershipEvent event) {
                HazelcastTransport.this.remotes = _remoteMembers(event.getMembers());
            }

            @Override
            public void memberRemoved(final MembershipEvent event) {
                HazelcastTransport.this.remotes = _remoteMembers(event.getMembers());
            }

            @Override
            public void memberAttributeChanged(final MemberAttributeEvent memberAttributeEvent) {
            }
        });
        log.infof("HazelcastRegistry started on address: [%s]", this.local); //TODO Message
    }

    @Override
    public void close() throws Exception {
        log.infof("HazelcastRegistry is shutting down."); //TODO Message
        this.executor.shutdown();
        this.hazelcast.shutdown();
        super.close();
    }

    @Override
    public ExecutableId generateExecutableId() {
        return new HazelcastUUIDId(local);
    }

    @Override
    public WorkerId generateWorkerId(final Worker worker) {
        if (worker instanceof Thread) {
            return new HazelcastWorkerId((Thread)worker, local);
        } else {
            return new HazelcastUUIDId(local);
        }
    }

    @Override
    public ExecutionRepositoryId generateExecutionRepositoryId() {
        return new HazelcastUUIDId(local);
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
    protected DistributedWorker<Member> createDistributedWorker(final Member address, final WorkerId workerId) {
        return new HazelcastWorker(this, this.local, address, workerId);
    }

    @Override
    protected DistributedProxyExecutionRepository<Member> createDistributedExecutionRepository(final ExecutionRepositoryId id, final Member address) {
        return new HazelcastProxyExecutionRepository(this, id, address);
    }

    @Override
    protected boolean isMatchingAddressType(final Object address) {
        return address instanceof Member;
    }

    @Override
    public <T> void invokeRemote(final Member address, final Command<T, Member> command, final Deferred<T, Throwable, ?> promise, final long timeout, final TimeUnit unit) {
        try {
            log.tracef("Invoking %s on %s.", command, address);
            final FutureDeferred<T, Void> run = new FutureDeferred<>(this.executor.<T>submitToMember(
                    new Invocation<>(command, this.local),
                    address
            ), timeout, unit);
            run.onResolve(promise)
                    .onReject(promise)
                    .onCancel(promise);
            network.execute(run);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    public static class Invocation<T> implements Callable<T>, Serializable, HazelcastInstanceAware {
        private static final long serialVersionUID = 1L;

        private final Command<T, Member> command;
        private final String uuid;
        private transient HazelcastTransport transport;
        private transient Member origin;

        public Invocation(final Command<T, Member> command, final Member origin) {
            this.command = command;
            this.uuid = origin.getUuid();
        }

        @Override
        public void setHazelcastInstance(final HazelcastInstance hazelcast) {
            this.transport = (HazelcastTransport) hazelcast.getUserContext().get(HazelcastTransport.class.getCanonicalName());
            for (final Member member : hazelcast.getCluster().getMembers()) {
                if (member.getUuid().equals(this.uuid)) {
                    this.origin = member;
                    break;
                }
            }
            if (this.origin == null) {
                throw new IllegalStateException(); //TODO Message
            }
        }

        @Override
        public T call() throws Exception {
            try {
                return command.perform(this.transport, this.origin);
            } catch (final Exception e) {
                throw e;
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
