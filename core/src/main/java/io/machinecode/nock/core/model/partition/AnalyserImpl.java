package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.Analyser;
import io.machinecode.nock.core.model.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AnalyserImpl extends PropertyReferenceImpl implements Analyser {

    public AnalyserImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
