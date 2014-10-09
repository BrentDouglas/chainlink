package io.machinecode.chainlink.transport.core;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class DistributedUUIDId<A> implements ExecutionRepositoryId, ExecutableId, ChainId, WorkerId {
    private static final long serialVersionUID = 1L;

    final UUID uuid;
    final A address;

    public DistributedUUIDId(final UUID uuid, final A address) {
        this.uuid = uuid;
        this.address = address;
    }

    public DistributedUUIDId(final A address) {
        this(UUID.randomUUID(), address);
    }

    @Override
    public A getAddress() {
        return address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DistributedUUIDId that = (DistributedUUIDId) o;
        return !(address != null ? !address.equals(that.address) : that.address != null) && uuid.equals(that.uuid);

    }

    @Override
    public int hashCode() {
        return 31 * uuid.hashCode() + (address != null ? address.hashCode() : 0);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[address=" + address + ",uuid=" + uuid + "]";
    }
}
