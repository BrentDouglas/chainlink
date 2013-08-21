package io.machinecode.nock.spi.inject;

import io.machinecode.nock.spi.context.Context;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Resolvable<T> {

    T resolve(final Context context, final ClassLoader loader) throws Exception;
}
