package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.spi.element.partition.Collector;

import javax.batch.api.partition.PartitionCollector;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CollectorImpl extends PropertyReferenceImpl<PartitionCollector> implements Collector {

    public CollectorImpl(final TypedArtifactReference<PartitionCollector> ref, final PropertiesImpl properties) {
        super(ref, properties);
    }
}
