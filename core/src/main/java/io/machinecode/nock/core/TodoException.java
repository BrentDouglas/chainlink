package io.machinecode.nock.core;

import javax.batch.operations.BatchRuntimeException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class TodoException extends BatchRuntimeException {

    public TodoException() {
        this("");
    }

    public TodoException(final String message) {
        super(_message(message));
    }

    public TodoException(final Throwable cause) {
        this("", cause);
    }

    public TodoException(final String message, final Throwable cause) {
        super(_message(message), cause);
    }

    private static String _message(final String message) {
        return new StringBuilder("") //TODO
                .append(message)
                .toString();
    }
}
