package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.spi.element.partition.Reducer;

import javax.batch.api.partition.PartitionReducer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ReducerImpl extends PropertyReferenceImpl<PartitionReducer> implements Reducer {

    public ReducerImpl(final String ref, final PropertiesImpl properties) {
        super(new ResolvableReference<PartitionReducer>(ref, PartitionReducer.class), properties);
    }
}
