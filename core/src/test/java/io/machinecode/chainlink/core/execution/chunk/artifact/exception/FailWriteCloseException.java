package io.machinecode.chainlink.core.execution.chunk.artifact.exception;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailWriteCloseException extends Exception {

    public FailWriteCloseException() {
    }

    public FailWriteCloseException(final String message) {
        super(message);
    }

    public FailWriteCloseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FailWriteCloseException(final Throwable cause) {
        super(cause);
    }

    public FailWriteCloseException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
