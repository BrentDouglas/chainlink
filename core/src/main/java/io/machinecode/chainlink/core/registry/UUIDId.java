package io.machinecode.chainlink.core.registry;

import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.transport.Addressed;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class UUIDId implements ExecutionRepositoryId, ExecutableId, ChainId, WorkerId {
    private static final long serialVersionUID = 1L;

    final UUID uuid;
    final Object address;

    public UUIDId(final UUID uuid, final Addressed addressed) {
        this.uuid = uuid;
        this.address = addressed.getAddress();
    }

    public UUIDId(final Addressed addressed) {
        this(UUID.randomUUID(), addressed);
    }

    @Override
    public Object getAddress() {
        return address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UUIDId that = (UUIDId) o;
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
