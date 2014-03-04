package io.machinecode.chainlink.spi;

import javax.batch.operations.NoSuchJobException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
