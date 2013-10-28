package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.spi.element.task.CheckpointAlgorithm;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CheckpointAlgorithmImpl extends PropertyReferenceImpl<javax.batch.api.chunk.CheckpointAlgorithm> implements CheckpointAlgorithm {

    public CheckpointAlgorithmImpl(final TypedArtifactReference<javax.batch.api.chunk.CheckpointAlgorithm> ref, final PropertiesImpl properties) {
        super(ref, properties);
    }
}
