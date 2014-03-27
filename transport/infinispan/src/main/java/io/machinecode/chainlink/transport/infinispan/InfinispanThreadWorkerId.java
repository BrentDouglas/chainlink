package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.spi.transport.WorkerId;
import org.infinispan.remoting.transport.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InfinispanThreadWorkerId implements WorkerId {
    final long id;
    final Address address;

    public InfinispanThreadWorkerId(final Thread thread, final Address address) {
        this.id = thread.getId();
        this.address = address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final InfinispanThreadWorkerId that = (InfinispanThreadWorkerId) o;
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
