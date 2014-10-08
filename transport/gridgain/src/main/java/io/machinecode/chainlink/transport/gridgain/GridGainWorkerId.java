package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.transport.core.DistributedWorkerId;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class GridGainWorkerId extends DistributedWorkerId<UUID> {

    public GridGainWorkerId(final Thread thread, final UUID address) {
        super(thread, address);
    }
}
