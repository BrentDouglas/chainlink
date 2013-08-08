package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.Collector;
import io.machinecode.nock.core.model.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CollectorImpl extends PropertyReferenceImpl implements Collector {

    public CollectorImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
