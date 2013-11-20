package io.machinecode.nock.spi.deferred;

import java.util.concurrent.ExecutionException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ResolvedException extends ExecutionException {

    public ResolvedException(final String message) {
        super(message);
    }
}
