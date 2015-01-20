package io.machinecode.chainlink.transport.jgroups;

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
import io.machinecode.then.api.OnCancel;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.api.OnReject;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.core.FutureDeferred;
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
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsTransport extends DistributedTransport<Address> implements AsyncRequestHandler, MembershipListener {

    private static final Logger log = Logger.getLogger(JGroupsTransport.class);

    final JChannel channel;
    final MessageDispatcher dispatcher;
    final Address local;
    protected volatile List<Address> remotes;

    public JGroupsTransport(final Dependencies dependencies, final Properties properties, final JChannel channel, final String clusterName) throws Exception {
        super(dependencies, properties);
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
        log.infof("JGroupsRegistry started on address: [%s]", this.local); //TODO Message
    }

    @Override
    public void close() throws Exception {
        log.infof("JGroupsRegistry is shutting down."); //TODO Message
        this.channel.close();
        super.close();
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
    public Address getAddress() {
        return local;
    }

    @Override
    protected List<Address> getRemotes() {
        return remotes;
    }

    @Override
    protected DistributedWorker<Address> createDistributedWorker(final Address address, final WorkerId workerId) {
        return new JGroupsWorker(this, this.local, address, workerId);
    }

    @Override
    protected DistributedProxyExecutionRepository<Address> createDistributedExecutionRepository(final ExecutionRepositoryId id, final Address address) {
        return new JGroupsProxyExecutionRepository(this, id, address);
    }

    @Override
    protected boolean isMatchingAddressType(final Object address) {
        return address instanceof Address;
    }

    @Override
    public <T> void invokeRemote(final Address address, final Command<T, Address> command,
                                 final Deferred<T, Throwable,?> promise, final long timeout, final TimeUnit unit) {
        try {
            log.tracef("Sending to %s: %s.", address, command);
            this.dispatcher.sendMessageWithFuture(
                    new Message(address, marshalling.marshall(command)),
                    RequestOptions.SYNC()
                            .setExclusionList(this.local)
                            .setTimeout(unit.toMillis(timeout)),
                    new JGroupsFutureListener<>(address, command, this.network, promise, timeout, unit)
            );
        } catch (final Exception e) {
            promise.reject(e);
        }
    }

    @Override
    public Object handle(final Message msg) throws Exception {
        try {
            final Address src = msg.src();
            @SuppressWarnings("unchecked")
            final Command<?,Address> command = marshalling.unmarshall(msg.getBuffer(), Command.class, this.loader.get());
            log.tracef("Starting from %s: %s.", src, command);
            final Object ret = command.perform(this, this.getRegistry(), src);
            log.tracef("Finished from %s: %s with %s.", src, command, ret);
            return ret;
        } catch (final Exception e) {
            throw e;
        } catch (final Throwable e) {
            throw new Exception(e);
        }
    }

    @Override
    public void handle(final Message msg, final Response response) throws Exception {
        final Address src = msg.src();
        @SuppressWarnings("unchecked")
        final Command<?,Address> command = marshalling.unmarshall(msg.getBuffer(), Command.class, this.loader.get());
        log.tracef("Starting async from %s: %s.", src, command);
        final Future<?> future = invokeLocal(command, src);
        final FutureDeferred<Object, Throwable> def = new FutureDeferred<>(future);
        final Listener listener = new Listener(src, command, response, future, timeout, unit);
        def.onResolve(listener)
                .onReject(listener)
                .onCancel(listener)
                .onComplete(listener);
        executor.execute(def.asRunnable());
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

    public static class Listener implements OnResolve<Object>, OnReject<Throwable>, OnCancel, OnComplete {

        final Address origin;
        final Command<?,Address> command;

        final Response response;
        final Future<?> future;
        final long timeout;
        final TimeUnit unit;

        public Listener(final Address origin, final Command<?, Address> command, final Response response, final Future<?> future, final long timeout, final TimeUnit unit) {
            this.origin = origin;
            this.command = command;
            this.response = response;
            this.future = future;
            this.timeout = timeout;
            this.unit = unit;
        }

        @Override
        public boolean cancel(final boolean mayInterrupt) {
            try {
                response.send(future.get(timeout, unit), false);
            } catch (final Throwable e) { //Should throw a CancellationException
                response.send(e, true);
            }
            return true;
        }

        @Override
        public void reject(final Throwable fail) {
            response.send(fail, true);
        }

        @Override
        public void resolve(final Object that) {
            response.send(that, false);
        }

        @Override
        public void complete(final int state) {
            log.tracef("Finished from %s: %s.", origin, command);
        }
    }
}
