package io.machinecode.chainlink.spi.schema;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobOperatorWithNameExistsException extends Exception {

    public JobOperatorWithNameExistsException() {
    }

    public JobOperatorWithNameExistsException(final String message) {
        super(message);
    }

    public JobOperatorWithNameExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JobOperatorWithNameExistsException(final Throwable cause) {
        super(cause);
    }

    public JobOperatorWithNameExistsException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
