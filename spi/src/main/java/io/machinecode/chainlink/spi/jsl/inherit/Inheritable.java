package io.machinecode.chainlink.spi.jsl.inherit;

import io.machinecode.chainlink.spi.loader.JobRepository;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Inheritable<T extends Inheritable<T>> extends Copyable<T> {

    /**
     *
     * @param repository
     */
    T inherit(JobRepository repository, String defaultJobXml);
}
