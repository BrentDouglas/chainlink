package io.machinecode.nock.spi.transport;

import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.Work;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Executable<T extends Work> extends Deferred<Void>, Serializable {

    T getWork();

    long getJobExecutionId();

    Result execute(Transport transport);
}