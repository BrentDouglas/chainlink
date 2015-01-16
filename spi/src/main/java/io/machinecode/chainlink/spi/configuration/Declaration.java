package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.configuration.factory.Factory;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Declaration<T> {

    Declaration<T> setValue(final T that);

    Declaration<T> setValueClass(final Class<? extends T> that);

    Declaration<T> setName(final String name);

    Declaration<T> setFactory(final Factory<? extends T> that);

    Declaration<T> setFactoryClass(final Class<? extends Factory<? extends T>> that);

    Declaration<T> setRef(final String fqcn);

    Declaration<T> setDefaultFactory(final Factory<? extends T> that);
}
