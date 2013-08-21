package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.descriptor.task.CheckpointAlgorithmImpl;
import io.machinecode.nock.core.descriptor.task.ChunkImpl;
import io.machinecode.nock.core.descriptor.task.ExceptionClassFilterImpl;
import io.machinecode.nock.core.descriptor.task.ItemProcessorImpl;
import io.machinecode.nock.core.descriptor.task.ItemReaderImpl;
import io.machinecode.nock.core.descriptor.task.ItemWriterImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.work.partition.CheckpointAlgorithmWork;
import io.machinecode.nock.core.work.task.ChunkWork;
import io.machinecode.nock.core.work.task.ExceptionClassFilterWork;
import io.machinecode.nock.core.work.task.ItemProcessorWork;
import io.machinecode.nock.core.work.task.ItemReaderWork;
import io.machinecode.nock.core.work.task.ItemWriterWork;
import io.machinecode.nock.spi.element.task.Chunk;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkFactory implements ElementFactory<Chunk, ChunkImpl, ChunkWork> {

    public static final ChunkFactory INSTANCE = new ChunkFactory();

    @Override
    public ChunkImpl produceDescriptor(final Chunk that, final JobPropertyContext context) {
        final String checkpointPolicy = Expression.resolveDescriptorProperty(that.getCheckpointPolicy(), context);
        final String itemCount = Expression.resolveDescriptorProperty(that.getItemCount(), context);
        final String timeLimit = Expression.resolveDescriptorProperty(that.getTimeLimit(), context);
        final String skipLimit = Expression.resolveDescriptorProperty(that.getSkipLimit(), context);
        final String retryLimit = Expression.resolveDescriptorProperty(that.getRetryLimit(), context);
        final ItemReaderImpl reader = ItemReaderFactory.INSTANCE.produceDescriptor(that.getReader(), context); //TODO Should not be null but needs validation
        final ItemProcessorImpl processor = ItemProcessorFactory.INSTANCE.produceDescriptor(that.getProcessor(), context);
        final ItemWriterImpl writer = ItemWriterFactory.INSTANCE.produceDescriptor(that.getWriter(), context);
        final CheckpointAlgorithmImpl checkpointAlgorithm = that.getCheckpointAlgorithm() == null
                ? null
                : CheckpointAlgorithmFactory.INSTANCE.produceDescriptor(that.getCheckpointAlgorithm(), context);
        final ExceptionClassFilterImpl skippableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceDescriptor(that.getSkippableExceptionClasses(), context);
        final ExceptionClassFilterImpl retryableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceDescriptor(that.getRetryableExceptionClasses(), context);
        final ExceptionClassFilterImpl noRollbackExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceDescriptor(that.getNoRollbackExceptionClasses(), context);
        return new ChunkImpl(
                checkpointPolicy,
                itemCount,
                timeLimit,
                skipLimit,
                retryLimit,
                reader,
                processor,
                writer,
                checkpointAlgorithm,
                skippableExceptionClasses,
                retryableExceptionClasses,
                noRollbackExceptionClasses
        );
    }

    @Override
    public ChunkWork produceExecution(final ChunkImpl that, final JobParameterContext context) {
        final String checkpointPolicy = Expression.resolveExecutionProperty(that.getCheckpointPolicy(), context);
        final String itemCount = Expression.resolveExecutionProperty(that.getItemCount(), context);
        final String timeLimit = Expression.resolveExecutionProperty(that.getTimeLimit(), context);
        final String skipLimit = Expression.resolveExecutionProperty(that.getSkipLimit(), context);
        final String retryLimit = Expression.resolveExecutionProperty(that.getRetryLimit(), context);
        final ItemReaderWork reader = ItemReaderFactory.INSTANCE.produceExecution(that.getReader(), context); //TODO Should not be null but needs validation
        final ItemProcessorWork processor = ItemProcessorFactory.INSTANCE.produceExecution(that.getProcessor(), context);
        final ItemWriterWork writer = ItemWriterFactory.INSTANCE.produceExecution(that.getWriter(), context);
        final CheckpointAlgorithmWork checkpointAlgorithm = that.getCheckpointAlgorithm() == null
                ? null
                : CheckpointAlgorithmFactory.INSTANCE.produceExecution(that.getCheckpointAlgorithm(), context);
        final ExceptionClassFilterWork skippableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceExecution(that.getSkippableExceptionClasses(), context);
        final ExceptionClassFilterWork retryableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceExecution(that.getRetryableExceptionClasses(), context);
        final ExceptionClassFilterWork noRollbackExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceExecution(that.getNoRollbackExceptionClasses(), context);
        return new ChunkWork(
                checkpointPolicy,
                itemCount,
                timeLimit,
                skipLimit,
                retryLimit,
                reader,
                processor,
                writer,
                checkpointAlgorithm,
                skippableExceptionClasses,
                retryableExceptionClasses,
                noRollbackExceptionClasses
        );
    }

    @Override
    public ChunkWork producePartitioned(final ChunkWork that, final PartitionPropertyContext context) {
        final String checkpointPolicy = Expression.resolvePartitionProperty(that.getCheckpointPolicy(), context);
        final String itemCount = Expression.resolvePartitionProperty(that.getItemCount(), context);
        final String timeLimit = Expression.resolvePartitionProperty(that.getTimeLimit(), context);
        final String skipLimit = Expression.resolvePartitionProperty(that.getSkipLimit(), context);
        final String retryLimit = Expression.resolvePartitionProperty(that.getRetryLimit(), context);
        final ItemReaderWork reader = ItemReaderFactory.INSTANCE.producePartitioned(that.getReader(), context); //TODO Should not be null but needs validation
        final ItemProcessorWork processor = ItemProcessorFactory.INSTANCE.producePartitioned(that.getProcessor(), context);
        final ItemWriterWork writer = ItemWriterFactory.INSTANCE.producePartitioned(that.getWriter(), context);
        final CheckpointAlgorithmWork checkpointAlgorithm = that.getCheckpointAlgorithm() == null
                ? null
                : CheckpointAlgorithmFactory.INSTANCE.producePartitioned(that.getCheckpointAlgorithm(), context);
        final ExceptionClassFilterWork skippableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.producePartitioned(that.getSkippableExceptionClasses(), context);
        final ExceptionClassFilterWork retryableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.producePartitioned(that.getRetryableExceptionClasses(), context);
        final ExceptionClassFilterWork noRollbackExceptionClasses = ExceptionClassFilterFactory.INSTANCE.producePartitioned(that.getNoRollbackExceptionClasses(), context);
        return new ChunkWork(
                checkpointPolicy,
                itemCount,
                timeLimit,
                skipLimit,
                retryLimit,
                reader,
                processor,
                writer,
                checkpointAlgorithm,
                skippableExceptionClasses,
                retryableExceptionClasses,
                noRollbackExceptionClasses
        );
    }
}
