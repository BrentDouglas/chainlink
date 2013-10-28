package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.spi.element.partition.Analyser;

import javax.batch.api.partition.PartitionAnalyzer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AnalyserImpl extends PropertyReferenceImpl<PartitionAnalyzer> implements Analyser {

    public AnalyserImpl(final TypedArtifactReference<PartitionAnalyzer> ref, final PropertiesImpl properties) {
        super(ref, properties);
    }
}
