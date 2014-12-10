package io.machinecode.chainlink.spi;

import javax.batch.operations.NoSuchJobException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ParentNotFoundException extends NoSuchJobException {

    public ParentNotFoundException() {
    }

    public ParentNotFoundException(final String message) {
        super(message);
    }

    public ParentNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ParentNotFoundException(final Throwable cause) {
        super(cause);
    }
}
