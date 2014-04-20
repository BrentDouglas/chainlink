package io.machinecode.chainlink.spi.exception;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NoConfigurationWithIdException extends RuntimeException {

    public NoConfigurationWithIdException(final String message) {
        super(message);
    }
}
