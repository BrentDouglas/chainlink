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
package io.machinecode.chainlink.spi.jsl.inherit.task;

import io.machinecode.chainlink.spi.jsl.inherit.Mergeable;
import io.machinecode.chainlink.spi.jsl.inherit.Rules;
import io.machinecode.chainlink.spi.jsl.task.CheckpointAlgorithm;
import io.machinecode.chainlink.spi.jsl.task.Chunk;
import io.machinecode.chainlink.spi.jsl.task.ExceptionClassFilter;
import io.machinecode.chainlink.spi.jsl.task.ItemProcessor;
import io.machinecode.chainlink.spi.jsl.task.ItemReader;
import io.machinecode.chainlink.spi.jsl.task.ItemWriter;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableChunk<T extends InheritableChunk<T, R, P, W, A, X>,
        R extends Mergeable<R> & ItemReader,
        P extends Mergeable<P> & ItemProcessor,
        W extends Mergeable<W> & ItemWriter,
        A extends Mergeable<A> & CheckpointAlgorithm,
        X extends Mergeable<X> & ExceptionClassFilter>
        extends InheritableTask<T>, Chunk {

    T setCheckpointPolicy(final String checkpointPolicy) ;

    T setItemCount(final String itemCount);

    T setTimeLimit(final String timeLimit);

    T setSkipLimit(final String skipLimit);

    T setRetryLimit(final String retryLimit);

    @Override
    R getReader();

    T setReader(final R reader);

    @Override
    P getProcessor();

    T setProcessor(final P processor);

    @Override
    W getWriter();

    T setWriter(final W writer);

    @Override
    A getCheckpointAlgorithm();

    T setCheckpointAlgorithm(final A checkpointAlgorithm);

    @Override
    X getSkippableExceptionClasses();

    T setSkippableExceptionClasses(final X skippableExceptionClasses);

    @Override
    X getRetryableExceptionClasses();

    T setRetryableExceptionClasses(final X retryableExceptionClasses);

    @Override
    X getNoRollbackExceptionClasses();

    T setNoRollbackExceptionClasses(final X noRollbackExceptionClasses);

    class ChunkTool {

        public static<T extends InheritableChunk<T, R, P, W, A, X>,
                R extends Mergeable<R> & ItemReader,
                P extends Mergeable<P> & ItemProcessor,
                W extends Mergeable<W> & ItemWriter,
                A extends Mergeable<A> & CheckpointAlgorithm,
                X extends Mergeable<X> & ExceptionClassFilter>
        T copy(final T _this, final T that) {
            that.setCheckpointPolicy(_this.getCheckpointPolicy());
            that.setItemCount(_this.getItemCount());
            that.setTimeLimit(_this.getTimeLimit());
            that.setSkipLimit(_this.getSkipLimit());
            that.setRetryLimit(_this.getRetryLimit());
            that.setReader(Rules.copy(_this.getReader()));
            that.setProcessor(Rules.copy(_this.getProcessor()));
            that.setWriter(Rules.copy(_this.getWriter()));
            that.setCheckpointAlgorithm(Rules.copy(_this.getCheckpointAlgorithm()));
            that.setSkippableExceptionClasses(Rules.copy(_this.getSkippableExceptionClasses()));
            that.setRetryableExceptionClasses(Rules.copy(_this.getRetryableExceptionClasses()));
            that.setNoRollbackExceptionClasses(Rules.copy(_this.getNoRollbackExceptionClasses()));
            return that;
        }

        public static<T extends InheritableChunk<T, R, P, W, A, X>,
                R extends Mergeable<R> & ItemReader,
                P extends Mergeable<P> & ItemProcessor,
                W extends Mergeable<W> & ItemWriter,
                A extends Mergeable<A> & CheckpointAlgorithm,
                X extends Mergeable<X> & ExceptionClassFilter>
        T merge(final T _this, final T that) {
            _this.setCheckpointPolicy(Rules.attributeRule(_this.getCheckpointPolicy(), that.getCheckpointPolicy()));
            _this.setItemCount(Rules.attributeRule(_this.getItemCount(), that.getItemCount()));
            _this.setTimeLimit(Rules.attributeRule(_this.getTimeLimit(), that.getTimeLimit()));
            _this.setSkipLimit(Rules.attributeRule(_this.getSkipLimit(), that.getSkipLimit()));
            _this.setRetryLimit(Rules.attributeRule(_this.getRetryLimit(), that.getRetryLimit()));
            _this.setReader(Rules.merge(_this.getReader(), that.getReader()));
            _this.setProcessor(Rules.merge(_this.getProcessor(), that.getProcessor()));
            _this.setWriter(Rules.merge(_this.getWriter(), that.getWriter()));
            _this.setCheckpointAlgorithm(Rules.merge(_this.getCheckpointAlgorithm(), that.getCheckpointAlgorithm()));
            _this.setSkippableExceptionClasses(Rules.merge(_this.getSkippableExceptionClasses(), that.getSkippableExceptionClasses()));
            _this.setRetryableExceptionClasses(Rules.merge(_this.getRetryableExceptionClasses(), that.getRetryableExceptionClasses()));
            _this.setNoRollbackExceptionClasses(Rules.merge(_this.getNoRollbackExceptionClasses(), that.getNoRollbackExceptionClasses()));
            return _this;
        }

    }
}
