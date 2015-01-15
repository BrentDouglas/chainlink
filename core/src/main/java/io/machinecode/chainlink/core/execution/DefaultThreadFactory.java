package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.configuration.PropertyLookup;

import java.util.concurrent.ThreadFactory;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class DefaultThreadFactory implements ThreadFactory, ThreadFactoryLookup {

    int id = 0;

    @Override
    public Thread newThread(final Runnable r) {
        return new Thread(r, "chainlink-" + "-"+ id++);
    }

    @Override
    public ThreadFactory lookupThreadFactory(final PropertyLookup properties) {
        return this;
    }
}
