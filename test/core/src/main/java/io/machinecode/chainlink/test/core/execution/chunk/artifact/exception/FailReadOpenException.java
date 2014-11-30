package io.machinecode.chainlink.test.core.execution.chunk.artifact.exception;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailReadOpenException extends Exception {

    public FailReadOpenException() {
    }

    public FailReadOpenException(final String message) {
        super(message);
    }

    public FailReadOpenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FailReadOpenException(final Throwable cause) {
        super(cause);
    }

    public FailReadOpenException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
