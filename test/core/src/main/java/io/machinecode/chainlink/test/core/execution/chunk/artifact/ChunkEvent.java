package io.machinecode.chainlink.test.core.execution.chunk.artifact;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public enum ChunkEvent {
    BEFORE_JOB,
    BEFORE_STEP,
    BEFORE_CHUNK,
    ON_CHUNK_ERROR,
    READER_OPEN,
    BEFORE_READ,
    READ,
    AFTER_READ,
    ON_READ_ERROR,
    RETRY_READ_EXCEPTION,
    SKIP_READ,
    READER_CHECKPOINT,
    READER_CLOSE,
    BEFORE_PROCESS,
    PROCESS,
    AFTER_PROCESS,
    ON_PROCESS_ERROR,
    RETRY_PROCESS_EXCEPTION,
    SKIP_PROCESS,
    WRITER_OPEN,
    BEFORE_WRITE,
    WRITE,
    AFTER_WRITE,
    ON_WRITE_ERROR,
    RETRY_WRITE_EXCEPTION,
    SKIP_WRITE,
    WRITER_CHECKPOINT,
    WRITER_CLOSE,
    AFTER_CHUNK,
    AFTER_STEP,
    AFTER_JOB,

    BEGIN_TRANSACTION,
    COMMIT_TRANSACTION,
    ROLLBACK_TRANSACTION
}
