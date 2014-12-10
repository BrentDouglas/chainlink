package io.machinecode.chainlink.spi.exception;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ResourceOrWrongTypeException extends RuntimeException {

    public ResourceOrWrongTypeException() {
    }

    public ResourceOrWrongTypeException(final String message) {
        super(message);
    }

    public ResourceOrWrongTypeException(final Throwable cause) {
        super(cause);
    }
}
