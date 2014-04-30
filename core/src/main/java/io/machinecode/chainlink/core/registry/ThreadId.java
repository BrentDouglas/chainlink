package io.machinecode.chainlink.core.registry;

import io.machinecode.chainlink.spi.registry.WorkerId;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ThreadId implements WorkerId {
    final long id;

    public ThreadId(final Thread thread) {
        this.id = thread.getId();
    }

    @Override
    public boolean equals(final Object that) {
        return this == that
                || (that instanceof ThreadId && id == ((ThreadId)that).id);
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + "]";
    }
}
