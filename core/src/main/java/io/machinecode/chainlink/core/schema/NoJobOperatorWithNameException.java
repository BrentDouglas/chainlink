package io.machinecode.chainlink.core.schema;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NoJobOperatorWithNameException extends Exception {

    public NoJobOperatorWithNameException() {
    }

    public NoJobOperatorWithNameException(final String message) {
        super(message);
    }

    public NoJobOperatorWithNameException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NoJobOperatorWithNameException(final Throwable cause) {
        super(cause);
    }

    public NoJobOperatorWithNameException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
