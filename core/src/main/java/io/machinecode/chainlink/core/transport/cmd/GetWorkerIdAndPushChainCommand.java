package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.transport.DistributedRemoteChain;
import io.machinecode.chainlink.core.transport.DistributedTransport;
import io.machinecode.chainlink.core.transport.RemoteWorkerAndChain;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Transport;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class GetWorkerIdAndPushChainCommand implements Command<RemoteWorkerAndChain> {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(GetWorkerIdAndPushChainCommand.class);

    final long jobExecutionId;
    final ExecutableId executableId;
    final ChainId chainId;

    public GetWorkerIdAndPushChainCommand(final long jobExecutionId, final ExecutableId executableId, final ChainId chainId) {
        this.jobExecutionId = jobExecutionId;
        this.executableId = executableId;
        this.chainId = chainId;
    }

    @Override
    public RemoteWorkerAndChain perform(final Configuration configuration, final Object origin) throws Throwable {
        log.tracef("Called perform with %s %s %s", jobExecutionId, executableId, chainId);
        final Worker worker = getLocalWorker(configuration, jobExecutionId, executableId);
        if (worker == null) {
            return null;
        }
        final Transport transport = configuration.getTransport();
        //TODO Remove need for cast
        final Chain<?> chain = new DistributedRemoteChain((DistributedTransport<?>)transport, origin, jobExecutionId, chainId);
        final ChainId remoteId = new UUIDId(transport);
        configuration.getRegistry().registerChain(jobExecutionId, remoteId, chain);
        return new RemoteWorkerAndChain(
                worker.getId(),
                remoteId
        );

    }

    private static Worker getLocalWorker(final Configuration configuration, final long jobExecutionId, final ExecutableId executableId) throws Exception {
        final Executable executable = configuration.getRegistry().getExecutable(jobExecutionId, executableId);
        if (executable == null) {
            return null;
        }
        return configuration.getExecutor().getWorker(executable.getWorkerId());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GetWorkerIdAndPushChainCommand{");
        sb.append("jobExecutionId=").append(jobExecutionId);
        sb.append(", executableId=").append(executableId);
        sb.append('}');
        return sb.toString();
    }
}
