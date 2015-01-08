package io.machinecode.chainlink.core.transport.artifacts;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestId implements ExecutableId, ExecutionRepositoryId, ChainId {

    final long id;
    final String address;

    public TestId(final long id, final String address) {
        this.id = id;
        this.address = address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TestId id = (TestId) o;

        if (this.id != id.id) return false;
        if (!address.equals(id.address)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }
}
