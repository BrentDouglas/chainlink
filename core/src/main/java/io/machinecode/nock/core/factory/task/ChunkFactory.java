package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.task.CheckpointAlgorithmImpl;
import io.machinecode.nock.core.model.task.ChunkImpl;
import io.machinecode.nock.core.model.task.ExceptionClassFilterImpl;
import io.machinecode.nock.core.model.task.ItemProcessorImpl;
import io.machinecode.nock.core.model.task.ItemReaderImpl;
import io.machinecode.nock.core.model.task.ItemWriterImpl;
import io.machinecode.nock.jsl.api.task.Chunk;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkFactory implements ElementFactory<Chunk, ChunkImpl> {

    public static final ChunkFactory INSTANCE = new ChunkFactory();

    @Override
    public ChunkImpl produceBuildTime(final Chunk that, final JobPropertyContext context) {
        final String checkpointPolicy = Expression.resolveBuildTime(that.getCheckpointPolicy(), context);
        final String itemCount = Expression.resolveBuildTime(that.getItemCount(), context);
        final String timeLimit = Expression.resolveBuildTime(that.getTimeLimit(), context);
        final String skipLimit = Expression.resolveBuildTime(that.getSkipLimit(), context);
        final String retryLimit = Expression.resolveBuildTime(that.getRetryLimit(), context);
        final ItemReaderImpl reader = ItemReaderFactory.INSTANCE.produceBuildTime(that.getReader(), context); //TODO Should not be null but needs validation
        final ItemProcessorImpl processor = ItemProcessorFactory.INSTANCE.produceBuildTime(that.getProcessor(), context);
        final ItemWriterImpl writer = ItemWriterFactory.INSTANCE.produceBuildTime(that.getWriter(), context);
        final CheckpointAlgorithmImpl checkpointAlgorithm = that.getCheckpointAlgorithm() == null
                ? null
                : CheckpointAlgorithmFactory.INSTANCE.produceBuildTime(that.getCheckpointAlgorithm(), context);
        final ExceptionClassFilterImpl skippableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceBuildTime(that.getSkippableExceptionClasses(), context);
        final ExceptionClassFilterImpl retryableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceBuildTime(that.getRetryableExceptionClasses(), context);
        final ExceptionClassFilterImpl noRollbackExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceBuildTime(that.getNoRollbackExceptionClasses(), context);
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
    public ChunkImpl producePartitionTime(final Chunk that, final PartitionPropertyContext context) {
        final String checkpointPolicy = Expression.resolvePartition(that.getCheckpointPolicy(), context);
        final String itemCount = Expression.resolvePartition(that.getItemCount(), context);
        final String timeLimit = Expression.resolvePartition(that.getTimeLimit(), context);
        final String skipLimit = Expression.resolvePartition(that.getSkipLimit(), context);
        final String retryLimit = Expression.resolvePartition(that.getRetryLimit(), context);
        final ItemReaderImpl reader = ItemReaderFactory.INSTANCE.producePartitionTime(that.getReader(), context); //TODO Should not be null but needs validation
        final ItemProcessorImpl processor = ItemProcessorFactory.INSTANCE.producePartitionTime(that.getProcessor(), context);
        final ItemWriterImpl writer = ItemWriterFactory.INSTANCE.producePartitionTime(that.getWriter(), context);
        final CheckpointAlgorithmImpl checkpointAlgorithm = that.getCheckpointAlgorithm() == null
                ? null
                : CheckpointAlgorithmFactory.INSTANCE.producePartitionTime(that.getCheckpointAlgorithm(), context);
        final ExceptionClassFilterImpl skippableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.producePartitionTime(that.getSkippableExceptionClasses(), context);
        final ExceptionClassFilterImpl retryableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.producePartitionTime(that.getRetryableExceptionClasses(), context);
        final ExceptionClassFilterImpl noRollbackExceptionClasses = ExceptionClassFilterFactory.INSTANCE.producePartitionTime(that.getNoRollbackExceptionClasses(), context);
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
