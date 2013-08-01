package io.machinecode.nock.jsl.api.chunk;

import io.machinecode.nock.jsl.api.Part;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Chunk extends Part {

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
