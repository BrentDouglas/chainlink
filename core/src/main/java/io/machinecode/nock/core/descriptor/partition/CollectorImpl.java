package io.machinecode.nock.core.descriptor.partition;

import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.partition.Collector;
import io.machinecode.nock.core.descriptor.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CollectorImpl extends PropertyReferenceImpl implements Collector {

    public CollectorImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
