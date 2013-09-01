package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.transport.Synchronization;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SynchronisationImpl implements Synchronization {

    private final AtomicInteger sync = new AtomicInteger(0);

    @Override
    public void register() {
        sync.incrementAndGet();
    }

    @Override
    public int registered() {
        return sync.get();
    }

    @Override
    public void unRegister() {
        sync.decrementAndGet();
    }
}
