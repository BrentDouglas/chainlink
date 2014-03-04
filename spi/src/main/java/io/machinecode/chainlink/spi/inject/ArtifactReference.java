package io.machinecode.chainlink.spi.inject;

import io.machinecode.chainlink.spi.context.ExecutionContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ArtifactReference {

    String ref();

    <T> T load(final Class<T> clazz, final InjectionContext injectionContext, final ExecutionContext context) throws Exception;
}
