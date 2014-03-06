package io.machinecode.chainlink.spi;

import io.machinecode.chainlink.spi.loader.JobRepository;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Inheritable<T extends Inheritable<T>> extends Copyable<T> {

    /**
     *
     * @param repository
     */
    T inherit(JobRepository repository, String defaultJobXml);
}
