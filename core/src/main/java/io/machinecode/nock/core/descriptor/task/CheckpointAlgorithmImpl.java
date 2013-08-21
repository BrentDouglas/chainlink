package io.machinecode.nock.core.descriptor.task;

import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.task.CheckpointAlgorithm;
import io.machinecode.nock.core.descriptor.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CheckpointAlgorithmImpl extends PropertyReferenceImpl implements CheckpointAlgorithm {

    public CheckpointAlgorithmImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
