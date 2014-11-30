package io.machinecode.chainlink.spi.inject;

import io.machinecode.chainlink.spi.context.ExecutionContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ArtifactReference {

    /**
     * @return The identifier of this artifact.
     */
    String ref();

    /**
     * @param as The interface to load the artifact as.
     * @param injectionContext
     * @param context
     * @param <T> The type of the loaded artifact.
     * @return The artifact identified by {@link #ref()} or null if none can be found.
     * @throws ArtifactOfWrongTypeException If the artifact loaded does not implement {@param as}.
     * @throws Exception If an error occurs.
     */
    <T> T load(final Class<T> as, final InjectionContext injectionContext, final ExecutionContext context) throws Exception;
}
