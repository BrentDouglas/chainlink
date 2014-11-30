package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.transport.core.DistributedUUIDId;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class GridGainUUIDId extends DistributedUUIDId<UUID> {

    public GridGainUUIDId(final UUID uuid, final UUID address) {
        super(uuid, address);
    }

    public GridGainUUIDId(final UUID address) {
        super(address);
    }
}
