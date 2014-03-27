package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;
import org.infinispan.remoting.transport.Address;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InfinispanUUIDExecutionRepositoryId implements ExecutionRepositoryId {

    final UUID uuid;
    final Address address;

    public InfinispanUUIDExecutionRepositoryId(final UUID uuid, final Address address) {
        this.uuid = uuid;
        this.address = address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final InfinispanUUIDExecutionRepositoryId that = (InfinispanUUIDExecutionRepositoryId) o;
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
