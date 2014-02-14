package io.machinecode.nock.core.model.task;

import io.machinecode.nock.spi.util.Messages;

import javax.batch.api.chunk.CheckpointAlgorithm;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
final class ItemCheckpointAlgorithm implements CheckpointAlgorithm {

    final int timeout;
    final int target;
    int current;

    public ItemCheckpointAlgorithm(final int timeout, final int target) {
        this.timeout = timeout;
        this.target = target;
    }

    @Override
    public int checkpointTimeout() throws Exception {
        return timeout;
    }

    @Override
    public void beginCheckpoint() throws Exception {
        current = 0;
    }

    @Override
    public boolean isReadyToCheckpoint() throws Exception {
        if (current > target) {
            throw new IllegalStateException(Messages.format("NOCK-030000.item.checkpoint", current, target));
        }
        return target == ++current;
    }

    @Override
    public void endCheckpoint() throws Exception {
        //
    }
}
