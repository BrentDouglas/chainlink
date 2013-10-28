package io.machinecode.nock.spi.work;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Bucket implements Serializable {

    private final Item[] items;
    private volatile int itemCount;
    private final Serializable[] data;
    private volatile int dataCount;

    public Bucket(final int partitions) {
        this.items = new Item[partitions];
        this.data = new Serializable[partitions];
    }

    public final void give(final Serializable that) {
        synchronized (data) {
            data[dataCount++] = that;
        }
    }

    public final void give(final Item that) {
        synchronized (items) {
            items[itemCount++] = that;
        }
    }

    public Serializable[] data() {
        return data;
    }

    public Item[] items() {
        return items;
    }

    public static class Item {
        public final BatchStatus batchStatus;
        public final String exitStatus;

        public Item(final BatchStatus batchStatus, final String exitStatus) {
            this.batchStatus = batchStatus;
            this.exitStatus = exitStatus;
        }
    }
}
