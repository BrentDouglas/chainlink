package io.machinecode.chainlink.execution.infinispan;

import io.machinecode.chainlink.execution.infinispan.cmd.ExecuteCommand;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import org.infinispan.remoting.transport.Address;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RpcWorker implements Worker {

    final InfinispanExecutor executor;
    final Address local;
    final Address remote;
    final ThreadId threadId;

    public RpcWorker(final InfinispanExecutor executor, final Address local, final Address remote, final ThreadId threadId) {
        this.executor = executor;
        this.local = local;
        this.remote = remote;
        this.threadId = threadId;
    }

    @Override
    public ThreadId getThreadId() {
        return threadId;
    }

    @Override
    public void addExecutable(final ExecutableEvent event) {
        final UUID uuid = UUID.randomUUID();
        executor.registerDeferred(uuid, event.getExecutable(), 180L); //TODO this should be provided by the executable
        executor.invoke(local, new ExecuteCommand(InfinispanExecutor.CACHE_NAME, remote, uuid, threadId, event));
    }

    @Override
    public void start() {
        //no-op
    }

    @Override
    public void run() {
        //no-op
    }
}
