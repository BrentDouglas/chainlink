package io.machinecode.chainlink.spi.exception;

import javax.batch.operations.BatchRuntimeException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ConfigurationException extends BatchRuntimeException {

    public ConfigurationException() {
    }

    public ConfigurationException(final String message) {
        super(message);
    }

    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(final Throwable cause) {
        super(cause);
    }
}
