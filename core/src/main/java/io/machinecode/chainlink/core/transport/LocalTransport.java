package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.execution.CallbackEventImpl;
import io.machinecode.chainlink.core.execution.ExecutableEventImpl;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.then.AllChain;
import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.RejectedDeferred;
import io.machinecode.then.core.ResolvedDeferred;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class LocalTransport implements Transport {

    protected Configuration configuration;
    protected final long timeout;
    protected final TimeUnit unit;

    public LocalTransport(final Dependencies dependencies, final PropertyLookup properties) {
        this.timeout = Long.parseLong(properties.getProperty(Constants.TIMEOUT, Constants.Defaults.NETWORK_TIMEOUT));
        this.unit = TimeUnit.valueOf(properties.getProperty(Constants.TIMEOUT_UNIT, Constants.Defaults.NETWORK_TIMEOUT_UNIT));

    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        this.configuration = configuration;
    }

    @Override
    public void close() throws Exception {
        //
    }

    @Override
    public Promise<Chain<?>, Throwable, Object> distribute(final int maxThreads, final Executable... executables) throws Exception {
        try {
            return new ResolvedDeferred<Chain<?>, Throwable, Object>(localDistribute(configuration, maxThreads, executables));
        } catch (final Exception e) {
            return new RejectedDeferred<Chain<?>, Throwable, Object>(e);
        }
    }

    @Override
    public Promise<Chain<?>, Throwable, Object> callback(final ExecutableId executableId, final ExecutionContext context) throws Exception {
        try {
            return new ResolvedDeferred<Chain<?>, Throwable, Object>(localCallback(configuration, executableId, context));
        } catch (final Exception e) {
            return new RejectedDeferred<Chain<?>, Throwable, Object>(e);
        }
    }

    public static Chain<?> localDistribute(final Configuration configuration, final int maxThreads, final Executable[] executables) throws Exception {
        final Registry registry = configuration.getRegistry();
        final Transport transport = configuration.getTransport();
        final List<Worker> workers = configuration.getExecutor().getWorkers(maxThreads);
        ListIterator<Worker> it = workers.listIterator();
        final Chain<?>[] chains = new Chain[executables.length];
        int i = 0;
        for (final Executable executable : executables) {
            if (!it.hasNext()) {
                it = workers.listIterator();
            }
            final Chain<?> chain = new ChainImpl<Void>();
            final ChainId chainId = new UUIDId(transport);
            registry.registerChain(executable.getContext().getJobExecutionId(), chainId, chain);
            final Worker worker = it.next();
            worker.execute(new ExecutableEventImpl(executable, chainId));
            chains[i++] = chain;
        }
        return new AllChain<Executable>(chains);
    }

    public static Chain<?> localCallback(final Configuration configuration, final ExecutableId executableId, final ExecutionContext context) throws Exception {
        final long jobExecutionId = context.getJobExecutionId();
        final Registry registry = configuration.getRegistry();
        final Transport transport = configuration.getTransport();
        final Executable executable = registry.getExecutable(jobExecutionId, executableId);
        if (executable == null) {
            throw new Exception("No worker found with jobExecutionId=" + jobExecutionId + " and executableId" + executableId);
        }
        final WorkerId workerId = executable.getWorkerId();
        final Worker worker = configuration.getExecutor().getWorker(workerId);
        if (worker == null) {
            throw new Exception("No worker found with workerId=" + workerId);
        }
        final UUIDId id = new UUIDId(transport);
        final Chain<?> chain = new ChainImpl<>();
        registry.registerChain(jobExecutionId, id, chain);
        worker.callback(new CallbackEventImpl(jobExecutionId, executableId, id, context));
        return chain;
    }

    @Override
    public Repository getRepository(final RepositoryId id) throws Exception {
        // This should never be called.
        return configuration.getRegistry().getRepository(id);
    }

    @Override
    public Void getAddress() {
        return null;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return unit;
    }
}
