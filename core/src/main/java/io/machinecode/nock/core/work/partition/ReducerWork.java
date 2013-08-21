package io.machinecode.nock.core.work.partition;

import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.Work;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.partition.Reducer;

import javax.batch.api.partition.PartitionReducer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ReducerWork implements Work, Reducer {

    private final ResolvableReference<PartitionReducer> reducer;

    public ReducerWork(final String ref) {
        this.reducer = new ResolvableReference<PartitionReducer>(ref, PartitionReducer.class);
    }

    @Override
    public String getRef() {
        return this.reducer.ref();
    }

    @Override
    public Properties getProperties() {
        return null;
    }
}
