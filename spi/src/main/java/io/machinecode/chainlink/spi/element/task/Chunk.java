package io.machinecode.chainlink.spi.element.task;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Chunk extends Task {

    String ELEMENT = "chunk";

    String TEN = "10";
    String ZERO = "0";
    String MINUS_ONE = "-1";

    String getCheckpointPolicy();

    String getItemCount();

    String getTimeLimit();

    String getSkipLimit();

    String getRetryLimit();

    ItemReader getReader();

    ItemProcessor getProcessor();

    ItemWriter getWriter();

    CheckpointAlgorithm getCheckpointAlgorithm();

    ExceptionClassFilter getSkippableExceptionClasses();

    ExceptionClassFilter getRetryableExceptionClasses();

    ExceptionClassFilter getNoRollbackExceptionClasses();

    public static final class CheckpointPolicy {
        public static final String ITEM = "item";
        public static final String CUSTOM = "custom";
    }
}
