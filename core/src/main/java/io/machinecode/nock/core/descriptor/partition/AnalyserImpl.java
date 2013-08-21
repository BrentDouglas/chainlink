package io.machinecode.nock.core.descriptor.partition;

import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.partition.Analyser;
import io.machinecode.nock.core.descriptor.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AnalyserImpl extends PropertyReferenceImpl implements Analyser {

    public AnalyserImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
