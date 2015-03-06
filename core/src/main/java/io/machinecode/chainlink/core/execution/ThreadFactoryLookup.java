package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.property.PropertyLookup;

import java.util.concurrent.ThreadFactory;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ThreadFactoryLookup {
    ThreadFactory lookupThreadFactory(final PropertyLookup properties) throws Exception;
}
