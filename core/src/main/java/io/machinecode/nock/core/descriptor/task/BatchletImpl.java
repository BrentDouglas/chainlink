package io.machinecode.nock.core.descriptor.task;

import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.task.Batchlet;
import io.machinecode.nock.core.descriptor.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletImpl extends PropertyReferenceImpl implements Batchlet {

    public BatchletImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
