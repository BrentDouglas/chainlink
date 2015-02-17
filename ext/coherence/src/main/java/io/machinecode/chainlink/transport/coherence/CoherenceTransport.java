package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.AbstractInvocable;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.InvocationObserver;
import com.tangosol.net.InvocationService;
import com.tangosol.net.Member;
import com.tangosol.util.UID;
import io.machinecode.chainlink.core.transport.DistributedTransport;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.DeferredImpl;
import io.machinecode.then.core.RejectedDeferred;
import io.machinecode.then.core.SomeDeferred;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CoherenceTransport extends DistributedTransport<Member> {

    private static final Logger log = Logger.getLogger(CoherenceTransport.class);

    final String invocationServiceName;
    final InvocationService executor;

    final Member local;
    protected volatile List<Member> remotes;

    public CoherenceTransport(final Dependencies dependencies, final Properties properties) throws Exception {
        super(dependencies, properties);
        this.invocationServiceName = properties.getProperty(Constants.COHERENCE_INVOCATION_SERVICE, Constants.Defaults.COHERENCE_INVOCATION_SERVICE);
        final Cluster cluster = CacheFactory.ensureCluster();
        this.executor = (InvocationService) CacheFactory.getService(invocationServiceName);
        this.local = cluster.getLocalMember();
        this.remotes = _remoteMembers(cluster.getMemberSet());
        log.infof("CoherenceRegistry started on address: [%s]", this.local); //TODO Message
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        super.open(configuration);
        this.executor.setUserContext(configuration);
    }

    @Override
    public void close() throws Exception {
        log.infof("CoherenceRegistry is shutting down."); //TODO Message
        this.executor.shutdown();
        super.close();
    }

    @Override
    public Member getAddress() {
        return local;
    }

    @Override
    protected <T> Promise<? extends Iterable<T>,Throwable,Object> invokeEverywhere(final Command<T> command) {
        final List<Member> remotes = this.remotes;
        final List<Promise<T,Throwable,?>> promises = new ArrayList<>(remotes.size());
        for (final Member remote : remotes) {
            promises.add(invokeRemote(remote, command));
        }
        return new SomeDeferred<>(promises);
    }

    @Override
    public <T> Promise<T,Throwable,Object> invokeRemote(final Object address, final Command<T> command, final long timeout, final TimeUnit unit) {
        if (!(address instanceof Member)) {
            return new RejectedDeferred<T, Throwable, Object>(new Exception("Expected " + Member.class.getName() + ". Found " + address.getClass())); //TODO Message
        }
        final DeferredImpl<T,Throwable,Object> deferred = new DeferredImpl<>();
        try {
            log.tracef("Invoking %s on %s.", command, address);
            this.executor.execute(
                    new Invocation(command, this.local, invocationServiceName),
                    Collections.singleton(address),
                    new DeferredObserver<>(deferred)
            );
        } catch (final Throwable e) {
            deferred.reject(e);
        }
        return deferred;
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

        private final Command<?> command;
        private final UID uuid;
        private final String invocationServiceName;
        private transient Configuration configuration;
        private transient Member origin;

        public Invocation(final Command<?> command, final Member origin, final String invocationServiceName) {
            this.command = command;
            this.invocationServiceName = invocationServiceName;
            this.uuid = origin.getUid();
        }

        @Override
        public void run() {
            this.configuration = (Configuration) CacheFactory.getService(this.invocationServiceName).getUserContext();
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
                command.perform(this.configuration, this.origin);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
