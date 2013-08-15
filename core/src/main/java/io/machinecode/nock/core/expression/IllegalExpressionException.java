package io.machinecode.nock.core.expression;

/**
 * TODO Get message from messages.properties
 *
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class IllegalExpressionException extends RuntimeException {

    public IllegalExpressionException() {
    }

    public IllegalExpressionException(final String message) {
        super(message);
    }

    public IllegalExpressionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IllegalExpressionException(final Throwable cause) {
        super(cause);
    }

    public IllegalExpressionException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
