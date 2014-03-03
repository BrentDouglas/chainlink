package io.machinecode.nock.core.model.task;

import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.util.Messages;

import javax.batch.api.chunk.CheckpointAlgorithm;

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
    public int checkpointTimeout(final Executor executor, final ExecutionContext context) throws Exception {
        return timeout;
    }

    @Override
    public void beginCheckpoint(final Executor executor, final ExecutionContext context) throws Exception {
        current = 0;
    }

    @Override
    public boolean isReadyToCheckpoint(final Executor executor, final ExecutionContext context) throws Exception {
        if (current > target) {
            throw new IllegalStateException(Messages.format("NOCK-030000.item.checkpoint", current, target));
        }
        return target == ++current;
    }

    @Override
    public void endCheckpoint(final Executor executor, final ExecutionContext context) throws Exception {
        //
    }
}
