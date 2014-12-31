package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.core.transport.DistributedWorkerId;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainWorkerId extends DistributedWorkerId<UUID> {
    private static final long serialVersionUID = 1L;

    public GridGainWorkerId(final Thread thread, final UUID address) {
        super(thread, address);
    }
}
