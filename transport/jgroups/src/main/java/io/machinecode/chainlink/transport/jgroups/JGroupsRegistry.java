package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.transport.core.BaseDistributedRegistry;
import io.machinecode.chainlink.transport.core.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.transport.core.DistributedRegistry;
import io.machinecode.chainlink.transport.core.DistributedWorker;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.Message;
import org.jgroups.View;
import org.jgroups.blocks.AsyncRequestHandler;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.Response;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JGroupsRegistry extends BaseDistributedRegistry<Address,JGroupsRegistry> implements AsyncRequestHandler, MembershipListener, DistributedRegistry<Address,JGroupsRegistry> {

    private static final Logger log = Logger.getLogger(JGroupsRegistry.class);

    final JChannel channel;
    final MessageDispatcher dispatcher;
    final Address local;
    protected volatile List<Address> remotes;

    public JGroupsRegistry(final RegistryConfiguration configuration, final JChannel channel, final String clusterName) throws Exception {
        super(configuration);
        this.channel = channel;
        try {
            channel.connect(clusterName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        this.local = channel.getAddress();
        this.remotes = _remoteMembers(this.channel.getView().getMembers());
        this.dispatcher = new MessageDispatcher(channel, null, this);
        this.dispatcher.setRequestHandler(this);
    }

    @Override
    public void startup() {
        super.startup();
        log.infof("JGroupsRegistry started on address: [%s]", this.local); //TODO Message
    }

    @Override
    public void shutdown() {
        log.infof("JGroupsRegistry is shutting down."); //TODO Message
        this.channel.close();
        super.shutdown();
    }

    @Override
    public ExecutableId generateExecutableId() {
        return new JGroupsUUIDId(local);
    }

    @Override
    public WorkerId generateWorkerId(final Worker worker) {
        if (worker instanceof Thread) {
            return new JGroupsWorkerId((Thread)worker, local);
        } else {
            return new JGroupsUUIDId(local);
        }
    }

    @Override
    public ExecutionRepositoryId generateExecutionRepositoryId() {
        return new JGroupsUUIDId(local);
    }

    @Override
    public Address getLocal() {
        return local;
    }

    @Override
    protected List<Address> getRemotes() {
        return remotes;
    }

    @Override
    protected DistributedWorker<Address, JGroupsRegistry> createDistributedWorker(final Address address, final WorkerId workerId) {
        return new JGroupsWorker(this, this.local, address, workerId);
    }

    @Override
    protected DistributedProxyExecutionRepository<Address, JGroupsRegistry> createDistributedExecutionRepository(final ExecutionRepositoryId id, final Address address) {
        return new JGroupsProxyExecutionRepository(this, id, address);
    }

    @Override
    public <T> void invoke(final Address address, final DistributedCommand<T, Address, JGroupsRegistry> command,
                           final Promise<T, Throwable> promise) {
        try {
            log.tracef("Invoking %s on %s.", command, address);
            this.dispatcher.sendMessageWithFuture(
                    new Message(address, marshaller.marshall(command)),
                    RequestOptions.SYNC()
                            .setExclusionList(this.local),
                    new JGroupsFutureListener<T>(this.network, promise, this.timeout, this.unit)
            );
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @Override
    public <T> void invoke(final Address address, final DistributedCommand<T, Address, JGroupsRegistry> command,
                           final Promise<T, Throwable> promise, final long timeout, final TimeUnit unit) {
        try {
            log.tracef("Invoking %s on %s.", command, address);
            this.dispatcher.sendMessageWithFuture(
                    new Message(address, marshaller.marshall(command)),
                    RequestOptions.SYNC()
                            .setExclusionList(this.local)
                            .setTimeout(unit.toMillis(timeout)),
                    new JGroupsFutureListener<T>(this.network, promise, timeout, unit)
            );
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @Override
    public Object handle(final Message msg) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            final DistributedCommand<?,Address,JGroupsRegistry> command = marshaller.unmarshall(msg.getBuffer(), DistributedCommand.class);
            log.tracef("Handling %s from %s.", command, msg.src());
            return command.perform(this, msg.src());
        } catch (final Exception e) {
            throw e;
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(final Message request, final Response response) throws Exception {
        Object ret;
        boolean threw = false;
        try {
            ret = handle(request);
        } catch (final Throwable e) {
            ret = e;
            threw = true;
        }
        response.send(ret, threw);
    }

    @Override
    public void viewAccepted(final View view) {
        final List<Address> members = this.remotes = _remoteMembers(view.getMembers());
        final StringBuilder builder = new StringBuilder();
        final String nl = System.lineSeparator();
        builder.append("[").append(members.size() + 1).append("] {").append(nl);
        builder.append("\t").append(this.local).append(" *").append(nl);
        for (final Address member : members) {
            builder.append("\t").append(member).append(nl);
        }
        builder.append("}");
        log.infof("%s %s", this.channel.getClusterName(), builder); //TODO Message
    }

    @Override
    public void suspect(final Address suspected) {}

    @Override
    public void block() {}

    @Override
    public void unblock() {}
}
