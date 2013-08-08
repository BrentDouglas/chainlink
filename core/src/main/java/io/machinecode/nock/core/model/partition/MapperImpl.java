package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.core.model.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperImpl extends PropertyReferenceImpl implements Mapper {

    public MapperImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
