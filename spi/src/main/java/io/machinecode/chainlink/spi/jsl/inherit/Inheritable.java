package io.machinecode.chainlink.spi.jsl.inherit;

import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Inheritable<T extends Inheritable<T>> extends Copyable<T> {

    /**
     *
     * @param repository
     */
    T inherit(InheritableJobLoader repository, String defaultJobXml);
}
