/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.factory.task;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.jsl.impl.ListenersImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PartitionImpl;
import io.machinecode.chainlink.core.jsl.impl.task.CheckpointAlgorithmImpl;
import io.machinecode.chainlink.core.jsl.impl.task.ChunkImpl;
import io.machinecode.chainlink.core.jsl.impl.task.ExceptionClassFilterImpl;
import io.machinecode.chainlink.core.jsl.impl.task.ItemProcessorImpl;
import io.machinecode.chainlink.core.jsl.impl.task.ItemReaderImpl;
import io.machinecode.chainlink.core.jsl.impl.task.ItemWriterImpl;
import io.machinecode.chainlink.spi.jsl.task.Chunk;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChunkFactory {

    public static ChunkImpl produceExecution(final Chunk that, final ListenersImpl listeners, final PartitionImpl<?> partition, final JobPropertyContext context) {
        final String checkpointPolicy = Expression.resolveExecutionProperty(that.getCheckpointPolicy(), context);
        final String itemCount = Expression.resolveExecutionProperty(that.getItemCount(), context);
        final String timeLimit = Expression.resolveExecutionProperty(that.getTimeLimit(), context);
        final String skipLimit = Expression.resolveExecutionProperty(that.getSkipLimit(), context);
        final String retryLimit = Expression.resolveExecutionProperty(that.getRetryLimit(), context);
        final ItemReaderImpl reader = ItemReaderFactory.produceExecution(that.getReader(), context); //TODO Should not be null but needs validation
        final ItemProcessorImpl processor = that.getProcessor() == null
                ? null
                : ItemProcessorFactory.produceExecution(that.getProcessor(), context);
        final ItemWriterImpl writer = ItemWriterFactory.produceExecution(that.getWriter(), context);
        final CheckpointAlgorithmImpl checkpointAlgorithm = that.getCheckpointAlgorithm() == null
                ? null
                : CheckpointAlgorithmFactory.produceExecution(that.getCheckpointAlgorithm(), context);
        final ExceptionClassFilterImpl skippableExceptionClasses = ExceptionClassFilterFactory.produceExecution(that.getSkippableExceptionClasses(), context);
        final ExceptionClassFilterImpl retryableExceptionClasses = ExceptionClassFilterFactory.produceExecution(that.getRetryableExceptionClasses(), context);
        final ExceptionClassFilterImpl noRollbackExceptionClasses = ExceptionClassFilterFactory.produceExecution(that.getNoRollbackExceptionClasses(), context);
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
                noRollbackExceptionClasses,
                listeners,
                partition
        );
    }

    public static ChunkImpl producePartitioned(final ChunkImpl that, final ListenersImpl listeners, final PartitionImpl<?> partition, final PartitionPropertyContext context) {
        final String checkpointPolicy = Expression.resolvePartitionProperty(that.getCheckpointPolicy(), context);
        final String itemCount = Expression.resolvePartitionProperty(that.getItemCount(), context);
        final String timeLimit = Expression.resolvePartitionProperty(that.getTimeLimit(), context);
        final String skipLimit = Expression.resolvePartitionProperty(that.getSkipLimit(), context);
        final String retryLimit = Expression.resolvePartitionProperty(that.getRetryLimit(), context);
        final ItemReaderImpl reader = ItemReaderFactory.producePartitioned(that.getReader(), context); //TODO Should not be null but needs validation
        final ItemProcessorImpl processor = that.getProcessor() == null
                ? null
                : ItemProcessorFactory.producePartitioned(that.getProcessor(), context);
        final ItemWriterImpl writer = ItemWriterFactory.producePartitioned(that.getWriter(), context);
        final CheckpointAlgorithmImpl checkpointAlgorithm = that.getCheckpointAlgorithm() == null
                ? null
                : CheckpointAlgorithmFactory.producePartitioned(that.getCheckpointAlgorithm(), context);
        final ExceptionClassFilterImpl skippableExceptionClasses = ExceptionClassFilterFactory.producePartitioned(that.getSkippableExceptionClasses(), context);
        final ExceptionClassFilterImpl retryableExceptionClasses = ExceptionClassFilterFactory.producePartitioned(that.getRetryableExceptionClasses(), context);
        final ExceptionClassFilterImpl noRollbackExceptionClasses = ExceptionClassFilterFactory.producePartitioned(that.getNoRollbackExceptionClasses(), context);
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
                noRollbackExceptionClasses,
                listeners,
                partition
        );
    }
}
