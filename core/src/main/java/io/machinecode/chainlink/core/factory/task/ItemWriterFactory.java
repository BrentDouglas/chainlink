package io.machinecode.chainlink.core.factory.task;

import io.machinecode.chainlink.core.element.PropertiesImpl;
import io.machinecode.chainlink.core.element.task.ItemWriterImpl;
import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.spi.element.task.ItemWriter;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ItemWriterFactory implements ElementFactory<ItemWriter, ItemWriterImpl> {

    public static final ItemWriterFactory INSTANCE = new ItemWriterFactory();

    @Override
    public ItemWriterImpl produceExecution(final ItemWriter that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new ItemWriterImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }

    @Override
    public ItemWriterImpl producePartitioned(final ItemWriterImpl that, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new ItemWriterImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }
}
