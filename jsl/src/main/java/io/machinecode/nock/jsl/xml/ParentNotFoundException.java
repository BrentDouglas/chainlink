package io.machinecode.nock.jsl.xml;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ParentNotFoundException extends RuntimeException {

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

    public ParentNotFoundException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
