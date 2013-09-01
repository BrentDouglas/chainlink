package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.transport.Synchronization;

import java.io.Serializable;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Bucket implements Serializable {

    private final Serializable[] data;
    private final Synchronization synchronization;
    private volatile int count;

    public Bucket(final Serializable[] data, final Synchronization synchronization) {
        this.data = data;
        this.synchronization = synchronization;
        for (int i = 0; i < data.length; ++i) {
            synchronization.register();
        }
    }

    public final void give(final Serializable that) {
        synchronized (data) {
            data[count++] = that;
            synchronization.unRegister();
        }
    }

    public Serializable[] take() {
        return data;
    }
}
