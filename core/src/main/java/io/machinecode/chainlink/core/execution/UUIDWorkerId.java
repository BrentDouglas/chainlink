package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.transport.WorkerId;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class UUIDWorkerId implements WorkerId {
    final UUID uuid;

    public UUIDWorkerId(final UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(final Object that) {
        return this == that
                || (that instanceof UUIDWorkerId && uuid.equals(((UUIDWorkerId)that).uuid));
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "UUIDWorkerId[uuid=" + uuid + "]";
    }
}
