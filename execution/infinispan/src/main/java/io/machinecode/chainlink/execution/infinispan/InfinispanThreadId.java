package io.machinecode.chainlink.execution.infinispan;

import io.machinecode.chainlink.spi.context.ThreadId;
import org.infinispan.remoting.transport.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InfinispanThreadId implements ThreadId {
    final ThreadId threadId;
    final Address address;

    public InfinispanThreadId(final ThreadId threadId, final Address address) {
        this.threadId = threadId;
        this.address = address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final InfinispanThreadId that = (InfinispanThreadId) o;
        if (!address.equals(that.address)) return false;
        if (!threadId.equals(that.threadId)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = threadId.hashCode();
        result = 31 * result + address.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InfinispanThreadId[address="+ address+",threadId=" + threadId + "]";
    }
}
