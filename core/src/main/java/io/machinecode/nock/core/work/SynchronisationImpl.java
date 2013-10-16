package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.transport.Synchronization;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SynchronisationImpl implements Synchronization {

    private final AtomicInteger sync = new AtomicInteger(0);
    private final List<Object> notify = new ArrayList<Object>();

    @Override
    public void take() {
        sync.incrementAndGet();
    }

    @Override
    public boolean available() {
        return sync.get() == 0;
    }

    @Override
    public void release() {
        sync.decrementAndGet();
        if (available()) {
            for (final Object that : notify) {
                synchronized (that) {
                    that.notifyAll();
                }
            }
        }
    }

    @Override
    public void listener(final Object that) {
        synchronized (notify) {
            notify.add(that);
        }
    }
}
