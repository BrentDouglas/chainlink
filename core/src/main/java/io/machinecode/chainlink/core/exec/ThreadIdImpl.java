package io.machinecode.chainlink.core.exec;

import io.machinecode.chainlink.spi.context.ThreadId;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ThreadIdImpl implements ThreadId {
    final long id;

    public ThreadIdImpl(final Thread thread) {
        this.id = thread.getId();
    }

    @Override
    public boolean equals(final Object that) {
        return this == that
                || (that instanceof ThreadIdImpl && id == ((ThreadIdImpl)that).id);
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "ThreadIdImpl[id=" + id + "]";
    }
}
