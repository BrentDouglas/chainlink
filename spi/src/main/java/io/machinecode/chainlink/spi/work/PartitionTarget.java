package io.machinecode.chainlink.spi.work;

import io.machinecode.chainlink.spi.execution.Executable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PartitionTarget {
    public final Executable[] executables;
    public final int threads;

    public PartitionTarget(final Executable[] executables, final int threads) {
        this.executables = executables;
        this.threads = threads;
    }
}