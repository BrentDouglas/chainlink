package io.machinecode.chainlink.core.schema;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DeploymentWithNameExistsException extends Exception {

    public DeploymentWithNameExistsException() {
    }

    public DeploymentWithNameExistsException(final String message) {
        super(message);
    }

    public DeploymentWithNameExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DeploymentWithNameExistsException(final Throwable cause) {
        super(cause);
    }

    public DeploymentWithNameExistsException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
