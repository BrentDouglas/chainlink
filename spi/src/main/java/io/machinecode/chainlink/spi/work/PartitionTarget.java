package io.machinecode.chainlink.spi.work;

import io.machinecode.chainlink.spi.execution.Executable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class PartitionTarget {
    private final Executable[] executables;
    private final int threads;

    public PartitionTarget(final Executable[] executables, final int threads) {
        this.executables = executables;
        this.threads = threads;
    }

    public Executable[] getExecutables() {
        return executables;
    }

    public int getThreads() {
        return threads;
    }
}
