package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.transport.Addressed;

import java.io.Serializable;

/**
 * <p>An identifier for a {@link Worker}.</p>
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface WorkerId extends Addressed, Serializable {
}
