package io.machinecode.nock.spi.transport;

import io.machinecode.nock.spi.work.Deferred;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Executable extends Deferred<Void>, Serializable {

    long getJobExecutionId();

    Result execute(Transport transport);
}