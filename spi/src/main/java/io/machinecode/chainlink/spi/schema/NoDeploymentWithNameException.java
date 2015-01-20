package io.machinecode.chainlink.spi.schema;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NoDeploymentWithNameException extends Exception {

    public NoDeploymentWithNameException() {
    }

    public NoDeploymentWithNameException(final String message) {
        super(message);
    }

    public NoDeploymentWithNameException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NoDeploymentWithNameException(final Throwable cause) {
        super(cause);
    }

    public NoDeploymentWithNameException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
