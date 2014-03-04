package io.machinecode.chainlink.core.factory.task;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.loader.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.model.PropertiesImpl;
import io.machinecode.chainlink.core.model.task.ItemReaderImpl;
import io.machinecode.chainlink.spi.element.task.ItemReader;
import io.machinecode.chainlink.spi.factory.ElementFactory;
import io.machinecode.chainlink.spi.factory.JobPropertyContext;
import io.machinecode.chainlink.spi.factory.PropertyContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderFactory implements ElementFactory<ItemReader, ItemReaderImpl> {

    public static final ItemReaderFactory INSTANCE = new ItemReaderFactory();

    @Override
    public ItemReaderImpl produceExecution(final ItemReader that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new ItemReaderImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }

    @Override
    public ItemReaderImpl producePartitioned(final ItemReaderImpl that, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new ItemReaderImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }
}
