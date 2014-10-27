package io.machinecode.chainlink.test.core.execution.artifact.chunk.exception;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class FailReadCloseException extends Exception {

    public FailReadCloseException() {
    }

    public FailReadCloseException(final String message) {
        super(message);
    }

    public FailReadCloseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FailReadCloseException(final Throwable cause) {
        super(cause);
    }

    public FailReadCloseException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
