package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.registry.WorkerId;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class DistributedWorkerId<A> implements WorkerId {
    private static final long serialVersionUID = 1L;

    final long id;
    final A address;

    public DistributedWorkerId(final Thread thread, final A address) {
        if (thread == null) {
            throw new IllegalArgumentException(); //TODO Message
        }
        if (address == null) {
            throw new IllegalArgumentException(); //TODO Message
        }
        this.id = thread.getId();
        this.address = address;
    }

    public long getId() {
        return id;
    }

    @Override
    public A getAddress() {
        return address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DistributedWorkerId that = (DistributedWorkerId) o;
        return id == that.id && !(address != null ? !address.equals(that.address) : that.address != null);
    }

    @Override
    public int hashCode() {
        return 31 * (int) (id ^ (id >>> 32)) + (address != null ? address.hashCode() : 0);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[address=" + address + ",id=" + id + "]";
    }
}