package io.machinecode.nock.spi.loader;

import javax.batch.operations.BatchRuntimeException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
