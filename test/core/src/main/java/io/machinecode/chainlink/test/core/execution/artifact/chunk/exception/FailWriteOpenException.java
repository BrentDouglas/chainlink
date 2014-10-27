package io.machinecode.chainlink.test.core.execution.artifact.chunk.exception;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class FailWriteOpenException extends Exception {

    public FailWriteOpenException() {
    }

    public FailWriteOpenException(final String message) {
        super(message);
    }

    public FailWriteOpenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FailWriteOpenException(final Throwable cause) {
        super(cause);
    }

    public FailWriteOpenException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
