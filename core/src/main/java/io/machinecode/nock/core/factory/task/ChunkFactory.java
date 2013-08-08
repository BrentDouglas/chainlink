package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.task.CheckpointAlgorithmImpl;
import io.machinecode.nock.core.model.task.ChunkImpl;
import io.machinecode.nock.core.model.task.ExceptionClassFilterImpl;
import io.machinecode.nock.core.model.task.ItemProcessorImpl;
import io.machinecode.nock.core.model.task.ItemReaderImpl;
import io.machinecode.nock.core.model.task.ItemWriterImpl;
import io.machinecode.nock.jsl.api.task.Chunk;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkFactory implements ElementFactory<Chunk, ChunkImpl> {

    public static final ChunkFactory INSTANCE = new ChunkFactory();

    @Override
    public ChunkImpl produceBuildTime(final Chunk that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String checkpointPolicy = Expression.resolveBuildTime(that.getCheckpointPolicy(), jobProperties);
        final String itemCount = Expression.resolveBuildTime(that.getItemCount(), jobProperties);
        final String timeLimit = Expression.resolveBuildTime(that.getTimeLimit(), jobProperties);
        final String skipLimit = Expression.resolveBuildTime(that.getSkipLimit(), jobProperties);
        final String retryLimit = Expression.resolveBuildTime(that.getRetryLimit(), jobProperties);
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
    public ChunkImpl produceStartTime(final Chunk that, final Properties parameters) {
        final String checkpointPolicy = Expression.resolveStartTime(that.getCheckpointPolicy(), parameters);
        final String itemCount = Expression.resolveStartTime(that.getItemCount(), parameters);
        final String timeLimit = Expression.resolveStartTime(that.getTimeLimit(), parameters);
        final String skipLimit = Expression.resolveStartTime(that.getSkipLimit(), parameters);
        final String retryLimit = Expression.resolveStartTime(that.getRetryLimit(), parameters);
        final ItemReaderImpl reader = ItemReaderFactory.INSTANCE.produceStartTime(that.getReader(), parameters); //TODO Should not be null but needs validation
        final ItemProcessorImpl processor = ItemProcessorFactory.INSTANCE.produceStartTime(that.getProcessor(), parameters);
        final ItemWriterImpl writer = ItemWriterFactory.INSTANCE.produceStartTime(that.getWriter(), parameters);
        final CheckpointAlgorithmImpl checkpointAlgorithm = that.getCheckpointAlgorithm() == null
                ? null
                : CheckpointAlgorithmFactory.INSTANCE.produceStartTime(that.getCheckpointAlgorithm(), parameters);
        final ExceptionClassFilterImpl skippableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceStartTime(that.getSkippableExceptionClasses(), parameters);
        final ExceptionClassFilterImpl retryableExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceStartTime(that.getRetryableExceptionClasses(), parameters);
        final ExceptionClassFilterImpl noRollbackExceptionClasses = ExceptionClassFilterFactory.INSTANCE.produceStartTime(that.getNoRollbackExceptionClasses(), parameters);
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
    public ChunkImpl producePartitionTime(final Chunk that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String checkpointPolicy = Expression.resolvePartition(that.getCheckpointPolicy(), partitionPlan);
        final String itemCount = Expression.resolvePartition(that.getItemCount(), partitionPlan);
        final String timeLimit = Expression.resolvePartition(that.getTimeLimit(), partitionPlan);
        final String skipLimit = Expression.resolvePartition(that.getSkipLimit(), partitionPlan);
        final String retryLimit = Expression.resolvePartition(that.getRetryLimit(), partitionPlan);
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
