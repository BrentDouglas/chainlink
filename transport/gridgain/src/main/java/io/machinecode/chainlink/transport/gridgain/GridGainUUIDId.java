package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.core.transport.DistributedUUIDId;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainUUIDId extends DistributedUUIDId<UUID> {
    private static final long serialVersionUID = 1L;

    public GridGainUUIDId(final UUID uuid, final UUID address) {
        super(uuid, address);
    }

    public GridGainUUIDId(final UUID address) {
        super(address);
    }
}
