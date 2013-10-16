package io.machinecode.nock.spi.transport;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Result extends Serializable {

    Status status();

    Serializable value();

    public enum Status {
        CANCELLED,
        RUNNING,
        FINISHED,
        ERROR
    }
}
