package io.machinecode.nock.spi.inject;

import io.machinecode.nock.spi.context.Context;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Resolver {

    <T> T resolve(String id, Class<T> clazz, Context context);
}
