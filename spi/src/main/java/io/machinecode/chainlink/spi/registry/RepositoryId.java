package io.machinecode.chainlink.spi.registry;

import io.machinecode.chainlink.spi.transport.Addressed;

import java.io.Serializable;

/**
 * <p>An identifier for a {@link io.machinecode.chainlink.spi.repository.Repository}.</p>
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface RepositoryId extends Addressed, Serializable {
}
