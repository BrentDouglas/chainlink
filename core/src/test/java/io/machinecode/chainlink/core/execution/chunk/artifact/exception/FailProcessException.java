package io.machinecode.chainlink.core.execution.chunk.artifact.exception;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailProcessException extends Exception {

    public FailProcessException() {
    }

    public FailProcessException(final String message) {
        super(message);
    }

    public FailProcessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FailProcessException(final Throwable cause) {
        super(cause);
    }

    public FailProcessException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
