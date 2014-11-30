package io.machinecode.chainlink.spi.inject;

import javax.batch.operations.BatchRuntimeException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ArtifactOfWrongTypeException extends BatchRuntimeException {

    public ArtifactOfWrongTypeException() {
    }

    public ArtifactOfWrongTypeException(final String message) {
        super(message);
    }

    public ArtifactOfWrongTypeException(final Throwable cause) {
        super(cause);
    }

    public ArtifactOfWrongTypeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
