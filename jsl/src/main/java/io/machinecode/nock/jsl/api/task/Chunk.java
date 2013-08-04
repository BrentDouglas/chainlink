package io.machinecode.nock.jsl.api.task;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Chunk extends Task {

    String ELEMENT = "chunk";

    public static final class CheckpointPolicy {
        public static final String ITEM = "item";
        public static final String CUSTOM = "custom";
    }

    String getCheckpointPolicy();

    int getItemCount();

    int getTimeLimit();

    int getSkipLimit();

    int getRetryLimit();

    ItemReader getReader();

    ItemProcessor getProcessor();

    ItemWriter getWriter();

    CheckpointAlgorithm getCheckpointAlgorithm();

    ExceptionClassFilter getSkippableExceptionClasses();

    ExceptionClassFilter getRetryableExceptionClasses();

    ExceptionClassFilter getNoRollbackExceptionClasses();
}
