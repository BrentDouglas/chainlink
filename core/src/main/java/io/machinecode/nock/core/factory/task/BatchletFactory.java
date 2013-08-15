package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.task.BatchletImpl;
import io.machinecode.nock.jsl.api.task.Batchlet;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletFactory implements ElementFactory<Batchlet, BatchletImpl> {

    public static final BatchletFactory INSTANCE = new BatchletFactory();

    @Override
    public BatchletImpl produceBuildTime(final Batchlet that, final JobPropertyContext context) {
        final String ref = Expression.resolveBuildTime(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        return new BatchletImpl(ref, properties);
    }

    @Override
    public BatchletImpl producePartitionTime(final Batchlet that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartition(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitionTime(that.getProperties(), context);
        return new BatchletImpl(ref, properties);
    }
}
