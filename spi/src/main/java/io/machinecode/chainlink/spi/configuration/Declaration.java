package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.configuration.factory.Factory;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Declaration<T> {

    Declaration<T> setProperties(final Properties properties);

    Declaration<T> set(final T that);

    Declaration<T> setName(final String name);

    Declaration<T> setFactory(final Factory<? extends T> that);

    Declaration<T> setFactoryClass(final Class<? extends Factory<? extends T>> that);

    Declaration<T> setFactoryFqcn(final String fqcn);

    Declaration<T> setDefaultValueFactory(final Factory<? extends T> that);
}
