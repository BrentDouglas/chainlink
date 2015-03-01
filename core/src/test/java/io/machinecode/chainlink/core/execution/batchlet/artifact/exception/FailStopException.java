package io.machinecode.chainlink.core.execution.batchlet.artifact.exception;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailStopException extends Exception {

    public FailStopException() {
    }

    public FailStopException(final String message) {
        super(message);
    }

    public FailStopException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FailStopException(final Throwable cause) {
        super(cause);
    }

    public FailStopException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
