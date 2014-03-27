package io.machinecode.chainlink.core.element.task;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.util.Messages;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
final class ItemCheckpointAlgorithm extends CheckpointAlgorithmImpl {

    final int timeout;
    final int target;
    int current;

    public ItemCheckpointAlgorithm(final int timeout, final int target) {
        super(null, null);
        this.timeout = timeout;
        this.target = target;
    }

    @Override
    public int checkpointTimeout(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        return timeout;
    }

    @Override
    public void beginCheckpoint(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        current = 0;
    }

    @Override
    public boolean isReadyToCheckpoint(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        if (current > target) {
            throw new IllegalStateException(Messages.format("CHAINLINK-030000.item.checkpoint", current, target));
        }
        return target == ++current;
    }

    @Override
    public void endCheckpoint(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        //
    }
}
