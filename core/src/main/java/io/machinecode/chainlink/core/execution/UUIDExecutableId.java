package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.transport.ExecutableId;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class UUIDExecutableId implements ExecutableId, Serializable {

    final UUID uuid;

    public UUIDExecutableId(final UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UUIDExecutableId that = (UUIDExecutableId) o;
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
