package io.machinecode.chainlink.spi.exception;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class UnresolvableResourceException extends RuntimeException {

    public UnresolvableResourceException() {
    }

    public UnresolvableResourceException(final String message) {
        super(message);
    }

    public UnresolvableResourceException(final Throwable cause) {
        super(cause);
    }
}
