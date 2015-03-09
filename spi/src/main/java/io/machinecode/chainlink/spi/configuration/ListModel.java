package io.machinecode.chainlink.spi.configuration;

import java.util.Collection;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ListModel<T> {

    void set(final Collection<String> that);

    void add(final String that);

    Declaration<T> add();

    void clear();
}
