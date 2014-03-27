package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.transport.WorkerId;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ThreadWorkerId implements WorkerId {
    final long id;

    public ThreadWorkerId(final Thread thread) {
        this.id = thread.getId();
    }

    @Override
    public boolean equals(final Object that) {
        return this == that
                || (that instanceof ThreadWorkerId && id == ((ThreadWorkerId)that).id);
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "ThreadWorkerId[id=" + id + "]";
    }
}
