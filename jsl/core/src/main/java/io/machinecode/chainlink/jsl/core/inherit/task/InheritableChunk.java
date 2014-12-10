package io.machinecode.chainlink.jsl.core.inherit.task;

import io.machinecode.chainlink.jsl.core.inherit.Util;
import io.machinecode.chainlink.spi.Mergeable;
import io.machinecode.chainlink.spi.element.task.CheckpointAlgorithm;
import io.machinecode.chainlink.spi.element.task.Chunk;
import io.machinecode.chainlink.spi.element.task.ExceptionClassFilter;
import io.machinecode.chainlink.spi.element.task.ItemProcessor;
import io.machinecode.chainlink.spi.element.task.ItemReader;
import io.machinecode.chainlink.spi.element.task.ItemWriter;

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
            that.setReader(Util.copy(_this.getReader()));
            that.setProcessor(Util.copy(_this.getProcessor()));
            that.setWriter(Util.copy(_this.getWriter()));
            that.setCheckpointAlgorithm(Util.copy(_this.getCheckpointAlgorithm()));
            that.setSkippableExceptionClasses(Util.copy(_this.getSkippableExceptionClasses()));
            that.setRetryableExceptionClasses(Util.copy(_this.getRetryableExceptionClasses()));
            that.setNoRollbackExceptionClasses(Util.copy(_this.getNoRollbackExceptionClasses()));
            return that;
        }

        public static<T extends InheritableChunk<T, R, P, W, A, X>,
                R extends Mergeable<R> & ItemReader,
                P extends Mergeable<P> & ItemProcessor,
                W extends Mergeable<W> & ItemWriter,
                A extends Mergeable<A> & CheckpointAlgorithm,
                X extends Mergeable<X> & ExceptionClassFilter>
        T merge(final T _this, final T that) {
            _this.setCheckpointPolicy(Util.attributeRule(_this.getCheckpointPolicy(), that.getCheckpointPolicy()));
            _this.setItemCount(Util.attributeRule(_this.getItemCount(), that.getItemCount()));
            _this.setTimeLimit(Util.attributeRule(_this.getTimeLimit(), that.getTimeLimit()));
            _this.setSkipLimit(Util.attributeRule(_this.getSkipLimit(), that.getSkipLimit()));
            _this.setRetryLimit(Util.attributeRule(_this.getRetryLimit(), that.getRetryLimit()));
            _this.setReader(Util.merge(_this.getReader(), that.getReader()));
            _this.setProcessor(Util.merge(_this.getProcessor(), that.getProcessor()));
            _this.setWriter(Util.merge(_this.getWriter(), that.getWriter()));
            _this.setCheckpointAlgorithm(Util.merge(_this.getCheckpointAlgorithm(), that.getCheckpointAlgorithm()));
            _this.setSkippableExceptionClasses(Util.merge(_this.getSkippableExceptionClasses(), that.getSkippableExceptionClasses()));
            _this.setRetryableExceptionClasses(Util.merge(_this.getRetryableExceptionClasses(), that.getRetryableExceptionClasses()));
            _this.setNoRollbackExceptionClasses(Util.merge(_this.getNoRollbackExceptionClasses(), that.getNoRollbackExceptionClasses()));
            return _this;
        }

    }
}
