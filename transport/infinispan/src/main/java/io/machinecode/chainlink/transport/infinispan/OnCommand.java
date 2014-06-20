package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.then.api.OnCancel;
import io.machinecode.then.api.OnReject;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.PromiseImpl;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.remoting.transport.Address;

import java.util.concurrent.ExecutionException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class OnCommand implements OnResolve<Void>, OnReject<Throwable>, OnCancel {

    final Address address;
    final ReplicableCommand command;
    final InfinispanRegistry registry;

    public OnCommand(final Address address, final InfinispanRegistry registry, final ReplicableCommand command) {
        this.address = address;
        this.registry = registry;
        this.command = command;
    }

    @Override
    public boolean cancel(final boolean interrupt) {
        try {
            final PromiseImpl<Void> promise = new PromiseImpl<Void>();
            registry.invoke(address, command, promise);
            promise.get();
            return true;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reject(final Throwable throwable) {
        try {
            final PromiseImpl<Void> promise = new PromiseImpl<Void>();
            registry.invoke(address, command, promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resolve(final Void that) {
        try {
            final PromiseImpl<Void> promise = new PromiseImpl<Void>();
            registry.invoke(address, command, promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
