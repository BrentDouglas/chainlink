package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.core.deferred.LinkedDeferred;
import io.machinecode.chainlink.transport.infinispan.cmd.InvokeDeferredCommand;
import io.machinecode.chainlink.spi.transport.DeferredId;
import org.infinispan.remoting.transport.Address;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RemoteDeferred extends LinkedDeferred<Serializable> {

    final InfinispanTransport transport;
    final Address address;
    final long jobExecutionId;
    final DeferredId deferredId;

    public RemoteDeferred(final InfinispanTransport transport, final Address address, final long jobExecutionId, final DeferredId deferredId) {
        this.transport = transport;
        this.address = address;
        this.jobExecutionId = jobExecutionId;
        this.deferredId = deferredId;
    }

    private InvokeDeferredCommand _cmd(final String name, final boolean willReturn, final Serializable... params) {
        return new InvokeDeferredCommand(transport.cacheName, address, jobExecutionId, deferredId, name, willReturn, params);
    }

    @Override
    public void resolve(final Serializable that) {
        transport.invokeSync(address, _cmd("resolve", false, that));
    }

    @Override
    public void reject(final Throwable that) {
        transport.invokeSync(address, _cmd("reject", false, that));
    }

    @Override
    public boolean isResolved() {
        return (Boolean) transport.invokeSync(address, _cmd("isResolved", true));
    }

    @Override
    public boolean isRejected() {
        return (Boolean) transport.invokeSync(address, _cmd("isRejected", true));
    }

    @Override
    public Throwable getFailure() throws InterruptedException, ExecutionException {
        return (Throwable) transport.invokeSync(address, _cmd("getFailure", true));
    }

    @Override
    public Throwable getFailure(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (Throwable) transport.invokeSync(address, _cmd("getFailure", true, timeout, unit));
    }

    @Override
    public void await(final Lock lock, final Condition condition) throws InterruptedException {
        transport.invokeSync(address, _cmd("await", false));
    }

    @Override
    public void await(final long timeout, final TimeUnit unit, final Lock lock, final Condition condition) throws InterruptedException, TimeoutException {
        transport.invokeSync(address, _cmd("await", false, timeout, unit));
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return (Boolean) transport.invokeSync(address, _cmd("cancel", true, mayInterruptIfRunning));
    }

    @Override
    public boolean isCancelled() {
        return (Boolean) transport.invokeSync(address, _cmd("isCancelled", true));
    }

    @Override
    public boolean isDone() {
        return (Boolean) transport.invokeSync(address, _cmd("isDone", true));
    }

    @Override
    public Serializable get() throws InterruptedException, ExecutionException {
        return (Serializable) transport.invokeSync(address, _cmd("get", true));
    }

    @Override
    public Serializable get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (Serializable) transport.invokeSync(address, _cmd("get", true, timeout, unit));
    }
}
