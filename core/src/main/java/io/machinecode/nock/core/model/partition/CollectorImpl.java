package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.spi.element.partition.Collector;

import javax.batch.api.partition.PartitionCollector;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CollectorImpl extends PropertyReferenceImpl<PartitionCollector> implements Collector {

    public CollectorImpl(final String ref, final PropertiesImpl properties) {
        super(new ResolvableReference<PartitionCollector>(ref, PartitionCollector.class), properties);
    }
}
