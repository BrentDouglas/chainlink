package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.transport.core.DistributedWorkerId;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class GridGainWorkerId extends DistributedWorkerId<UUID> {

    public GridGainWorkerId(final Thread thread, final UUID address) {
        super(thread, address);
    }
}
