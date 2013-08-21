package io.machinecode.nock.core.work.partition;

import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.Work;
import io.machinecode.nock.spi.element.Properties;

import javax.batch.api.chunk.CheckpointAlgorithm;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CheckpointAlgorithmWork implements Work, io.machinecode.nock.spi.element.task.CheckpointAlgorithm {

    private final ResolvableReference<CheckpointAlgorithm> checkpointAlgorithm;

    public CheckpointAlgorithmWork(final String ref) {
        this.checkpointAlgorithm = new ResolvableReference<javax.batch.api.chunk.CheckpointAlgorithm>(ref, javax.batch.api.chunk.CheckpointAlgorithm.class);
    }

    @Override
    public String getRef() {
        return this.checkpointAlgorithm.ref();
    }

    @Override
    public Properties getProperties() {
        return null;
    }
}
