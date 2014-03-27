package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.transport.DeferredId;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class UUIDDeferredId implements DeferredId, Serializable {

    final UUID uuid;

    public UUIDDeferredId(final UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UUIDDeferredId that = (UUIDDeferredId) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[uuid=" + uuid + "]";
    }
}
