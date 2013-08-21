package io.machinecode.nock.core.descriptor.partition;

import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.partition.Reducer;
import io.machinecode.nock.core.descriptor.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ReducerImpl extends PropertyReferenceImpl implements Reducer {

    public ReducerImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
