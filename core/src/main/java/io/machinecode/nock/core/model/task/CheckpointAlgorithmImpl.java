package io.machinecode.nock.core.model.task;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.task.CheckpointAlgorithm;
import io.machinecode.nock.core.model.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CheckpointAlgorithmImpl extends PropertyReferenceImpl implements CheckpointAlgorithm {

    public CheckpointAlgorithmImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
