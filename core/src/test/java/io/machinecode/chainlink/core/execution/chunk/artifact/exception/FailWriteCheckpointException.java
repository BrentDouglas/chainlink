package io.machinecode.chainlink.core.execution.chunk.artifact.exception;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailWriteCheckpointException extends Exception {

    public FailWriteCheckpointException() {
    }

    public FailWriteCheckpointException(final String message) {
        super(message);
    }

    public FailWriteCheckpointException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FailWriteCheckpointException(final Throwable cause) {
        super(cause);
    }

    public FailWriteCheckpointException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
