package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.model.task.CheckpointAlgorithmImpl;
import io.machinecode.nock.core.model.task.ChunkImpl;
import io.machinecode.nock.core.model.task.ExceptionClassFilterImpl;
import io.machinecode.nock.core.model.task.ItemProcessorImpl;
import io.machinecode.nock.core.model.task.ItemReaderImpl;
import io.machinecode.nock.core.model.task.ItemWriterImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.spi.element.task.Chunk;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkFactory implements ElementFactory<Chunk, ChunkImpl> {

    public static final ChunkFactory INSTANCE = new ChunkFactory();

    @Override
    public ChunkImpl produceExecution(final Chunk that, final JobPropertyContext context) {
        final String checkpointPolicy = Expression.resolveExecutionProperty(that.getCheckpointPolicy(), context);
        final String itemCount = Expression.resolveExecutionProperty(that.getItemCount(), context);
        final String timeLimit = Expression.resolveExecutionProperty(that.getTimeLimit(), context);
        final String skipLimit = Expression.resolveExecutionProperty(that.getSkipLimit(), context);
        final String retryLimit = Expression.resolveExecutionProperty(that.getRetryLimit(), context);
        final ItemReaderImpl reader = ItemReaderFactory.INSTANCE.produceExecution(that.getReader(), context); //TODO Should not be null but needs validation
        final ItemProcessorImpl processor = ItemProcessorFactory.INSTANCE.produceExecution(that.getProcessor(), context);
        final ItemWriterImpl writer = ItemWriterFactory.INSTANCE.produceExecution(that.getWriter(), context);
        final CheckpointAlgorithmImpl checkpointAlgorithm = that.getCheckpointAlgorithm() == null
                ? null
                : CheckpointAlgorithmFactory.INSTANCE.produceExecution(that.getCheckpointAlgorithm(), context);
        final ExceptionClassFilterImpl skippableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceExecution(that.getSkippableExceptionClasses(), context);
        final ExceptionClassFilterImpl retryableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceExecution(that.getRetryableExceptionClasses(), context);
        final ExceptionClassFilterImpl noRollbackExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceExecution(that.getNoRollbackExceptionClasses(), context);
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
    public ChunkImpl producePartitioned(final ChunkImpl that, final PartitionPropertyContext context) {
        final String checkpointPolicy = Expression.resolvePartitionProperty(that.getCheckpointPolicy(), context);
        final String itemCount = Expression.resolvePartitionProperty(that.getItemCount(), context);
        final String timeLimit = Expression.resolvePartitionProperty(that.getTimeLimit(), context);
        final String skipLimit = Expression.resolvePartitionProperty(that.getSkipLimit(), context);
        final String retryLimit = Expression.resolvePartitionProperty(that.getRetryLimit(), context);
        final ItemReaderImpl reader = ItemReaderFactory.INSTANCE.producePartitioned(that.getReader(), context); //TODO Should not be null but needs validation
        final ItemProcessorImpl processor = ItemProcessorFactory.INSTANCE.producePartitioned(that.getProcessor(), context);
        final ItemWriterImpl writer = ItemWriterFactory.INSTANCE.producePartitioned(that.getWriter(), context);
        final CheckpointAlgorithmImpl checkpointAlgorithm = that.getCheckpointAlgorithm() == null
                ? null
                : CheckpointAlgorithmFactory.INSTANCE.producePartitioned(that.getCheckpointAlgorithm(), context);
        final ExceptionClassFilterImpl skippableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.producePartitioned(that.getSkippableExceptionClasses(), context);
        final ExceptionClassFilterImpl retryableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.producePartitioned(that.getRetryableExceptionClasses(), context);
        final ExceptionClassFilterImpl noRollbackExceptionClasses = ExceptionClassFilterFactory.INSTANCE.producePartitioned(that.getNoRollbackExceptionClasses(), context);
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
}
