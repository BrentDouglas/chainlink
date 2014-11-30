package io.machinecode.chainlink.spi;

import io.machinecode.chainlink.spi.loader.JobRepository;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Inheritable<T extends Inheritable<T>> extends Copyable<T> {

    /**
     *
     * @param repository
     */
    T inherit(JobRepository repository, String defaultJobXml);
}
