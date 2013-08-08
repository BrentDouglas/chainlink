package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.Reducer;
import io.machinecode.nock.core.model.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ReducerImpl extends PropertyReferenceImpl implements Reducer {

    public ReducerImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
