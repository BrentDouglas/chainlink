package io.machinecode.nock.spi.work;

import java.io.Serializable;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Bucket implements Serializable {

    private final Serializable[] data;
    private volatile int count;

    public Bucket(final Serializable[] data) {
        this.data = data;
    }

    public final void give(final Serializable that) {
        synchronized (data) {
            data[count++] = that;
        }
    }

    public Serializable[] take() {
        return data;
    }
}
