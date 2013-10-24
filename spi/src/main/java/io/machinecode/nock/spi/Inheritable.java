package io.machinecode.nock.spi;

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
