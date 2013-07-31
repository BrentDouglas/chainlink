package io.machinecode.nock.jsl.api.chunk;

import io.machinecode.nock.jsl.api.Part;
import io.machinecode.nock.jsl.api.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Chunk extends Part {

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

    Properties getProperties();
}
