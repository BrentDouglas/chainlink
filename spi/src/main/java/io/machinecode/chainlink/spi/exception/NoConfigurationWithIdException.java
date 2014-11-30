package io.machinecode.chainlink.spi.exception;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class NoConfigurationWithIdException extends RuntimeException {

    public NoConfigurationWithIdException(final String message) {
        super(message);
    }
}
