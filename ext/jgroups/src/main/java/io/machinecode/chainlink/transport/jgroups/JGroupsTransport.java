package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.core.transport.DistributedTransport;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.then.api.OnCancel;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.api.OnReject;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.RejectedDeferred;
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

import java.lang.ref.WeakReference;
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
    protected final Marshalling marshalling;
    protected final WeakReference<ClassLoader> loader;
    protected volatile List<Address> remotes;

    public JGroupsTransport(final Dependencies dependencies, final Properties properties, final JChannel channel) throws Exception {
        super(dependencies, properties);
        if (!(channel.isConnected() || channel.isConnecting())) {
            throw new IllegalStateException("Must already have called JChannel#connect(...)"); //TODO Message
        }
        this.channel = channel;
        this.loader = new WeakReference<>(dependencies.getClassLoader());
        this.marshalling = dependencies.getMarshalling();
        this.local = channel.getAddress();
        this.remotes = _remoteMembers(this.channel.getView().getMembers());
        this.dispatcher = new MessageDispatcher(channel, null, this);
        this.dispatcher.setRequestHandler(this);
        log.infof("JGroupsTransport %s has started.", this.local); //TODO Message
    }

    @Override
    public void close() throws Exception {
        log.infof("JGroupsTransport %s is shutting down.", this.local); //TODO Message
        super.close();
        this.dispatcher.setRequestHandler(null);
        this.channel.removeChannelListener(this.dispatcher);
    }

    @Override
    public Address getAddress() {
        return local;
    }

    @Override
    protected <T> Promise<? extends Iterable<T>,Throwable,Object> invokeEverywhere(final Command<T> command) {
        final List<Address> remotes = this.remotes;
        final JGroupsAnycastListener<T,Object> deferred = new JGroupsAnycastListener<>(this.local, command, timeout, unit);
        try {
            log.tracef("Sending from %s to all remotes: %s.", this.local, command);
            this.dispatcher.castMessageWithFuture(
                    remotes,
                    new Message(null, marshalling.marshall(command)),
                    RequestOptions.SYNC()
                            .setExclusionList(this.local)
                            .setTimeout(unit.toMillis(timeout)),
                    deferred
            );
        } catch (final Throwable e) {
            deferred.reject(e);
        }
        return deferred;
    }

    @Override
    public <T> Promise<T,Throwable,Object> invokeRemote(final Object address, final Command<T> command,
                                 final long timeout, final TimeUnit unit) {
        if (!(address instanceof Address)) {
            return new RejectedDeferred<T,Throwable,Object>(new Exception("Expected " + Address.class.getName() + ". Found " + address.getClass())); //TODO Message
        }
        final Address addr = (Address)address;
        final JGroupsListener<T,Object> deferred = new JGroupsListener<>(this.local, addr, command, timeout, unit);
        try {
            log.tracef("Node %s sending to %s: %s.", this.local, address, command);
            this.dispatcher.sendMessageWithFuture(
                    new Message(addr, marshalling.marshall(command)),
                    RequestOptions.SYNC()
                            .setExclusionList(this.local)
                            .setTimeout(unit.toMillis(timeout)),
                    deferred
            );
        } catch (final Throwable e) {
            deferred.reject(e);
        }
        return deferred;
    }

    @Override
    public Object handle(final Message msg) throws Exception {
        try {
            final Address src = msg.src();
            final Command<?> command = marshalling.unmarshall(msg.getBuffer(), Command.class, this.loader.get());
            log.tracef("Node %s starting from %s: %s.", local, src, command);
            final Object ret = command.perform(this.configuration, src);
            log.tracef("Node %s finished from %s: %s with %s.", local, src, command, ret);
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
        final Command<?> command = marshalling.unmarshall(msg.getBuffer(), Command.class, this.loader.get());
        log.tracef("Node %s starting async from %s: %s.", local, src, command);
        try {
            final Object ret = command.perform(configuration, src); //TODO Actually run this async
            response.send(ret, false);
        } catch (final Throwable e) { //Should throw a CancellationException
            response.send(e, true);
        }
        log.tracef("Node %s finished async from %s: %s.", local, src, command);
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

        final Address local;
        final Address origin;
        final Command<?> command;

        final Response response;
        final Future<?> future;
        final long timeout;
        final TimeUnit unit;

        public Listener(final Address local, final Address origin, final Command<?> command, final Response response, final Future<?> future, final long timeout, final TimeUnit unit) {
            this.local = local;
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
            log.tracef("Node %s finished from %s: %s.", local, origin, command);
        }
    }
}
