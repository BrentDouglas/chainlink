package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import org.jgroups.Address;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JGroupsUUIDId implements ExecutionRepositoryId, ExecutableId, ChainId, WorkerId {

    final UUID uuid;
    final Address address;

    public JGroupsUUIDId(final UUID uuid, final Address address) {
        this.uuid = uuid;
        this.address = address;
    }

    public JGroupsUUIDId(final Address address) {
        this(UUID.randomUUID(), address);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final JGroupsUUIDId that = (JGroupsUUIDId) o;
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
