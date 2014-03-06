package io.machinecode.chainlink.core.factory.task;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.element.PropertiesImpl;
import io.machinecode.chainlink.core.element.task.ItemProcessorImpl;
import io.machinecode.chainlink.spi.element.task.ItemProcessor;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemProcessorFactory implements ElementFactory<ItemProcessor, ItemProcessorImpl> {

    public static final ItemProcessorFactory INSTANCE = new ItemProcessorFactory();

    @Override
    public ItemProcessorImpl produceExecution(final ItemProcessor that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new ItemProcessorImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }

    @Override
    public ItemProcessorImpl producePartitioned(final ItemProcessorImpl that, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new ItemProcessorImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }
}
