package io.machinecode.chainlink.test.core.execution.chunk.artifact.exception;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class FailReadException extends Exception {

    public FailReadException() {
    }

    public FailReadException(final String message) {
        super(message);
    }

    public FailReadException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FailReadException(final Throwable cause) {
        super(cause);
    }

    public FailReadException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
