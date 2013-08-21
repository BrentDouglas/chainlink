package io.machinecode.nock.core.descriptor.partition;

import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.partition.Mapper;
import io.machinecode.nock.core.descriptor.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperImpl extends PropertyReferenceImpl implements Mapper {

    public MapperImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
