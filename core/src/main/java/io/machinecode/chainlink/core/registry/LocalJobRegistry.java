package io.machinecode.chainlink.core.registry;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.SplitAccumulator;
import io.machinecode.chainlink.spi.registry.StepAccumulator;
import io.machinecode.chainlink.spi.then.Chain;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class LocalJobRegistry {

    protected final TMap<ChainId, Chain<?>> chains;
    protected final TMap<ExecutableId, Executable> executables;
    protected final TMap<String, SplitAccumulator> splits;
    protected final TMap<String, StepAccumulator> steps;

    protected final AtomicBoolean chainLock = new AtomicBoolean(false);
    protected final AtomicBoolean executableLock = new AtomicBoolean(false);
    protected final AtomicBoolean splitLock = new AtomicBoolean(false);
    protected final AtomicBoolean stepLock = new AtomicBoolean(false);

    public LocalJobRegistry() {
        this.chains = new THashMap<ChainId, Chain<?>>();
        this.executables = new THashMap<ExecutableId, Executable>();
        this.splits = new THashMap<String, SplitAccumulator>();
        this.steps = new THashMap<String, StepAccumulator>();
    }

    public void registerChain(final ChainId id, final Chain<?> chain) {
        while (!chainLock.compareAndSet(false, true)) {}
        try {
            this.chains.put(id, chain);
        } finally {
            chainLock.set(false);
        }
    }

    public Chain<?> getChain(final ChainId id) {
        while (!chainLock.compareAndSet(false, true)) {}
        try {
            return this.chains.get(id);
        } finally {
            chainLock.set(false);
        }
    }

    public void registerExecutable(final ExecutableId id, final Executable executable) {
        while (!executableLock.compareAndSet(false, true)) {}
        try {
            this.executables.put(id, executable);
        } finally {
            executableLock.set(false);
        }
    }

    public Executable getExecutable(final ExecutableId id) {
        while (!executableLock.compareAndSet(false, true)) {}
        try {
            return this.executables.get(id);
        } finally {
            executableLock.set(false);
        }
    }

    public StepAccumulator getStepAccumulator(final String id) {
        while (!stepLock.compareAndSet(false, true)) {}
        try {
            StepAccumulator step = steps.get(id);
            if (step != null) {
                return step;
            }
            steps.put(id, step = new StepAccumulatorImpl());
            return step;
        } finally {
            stepLock.set(false);
        }
    }

    public SplitAccumulator getSplitAccumulator(final String id) {
        while (!splitLock.compareAndSet(false, true)) {}
        try {
            SplitAccumulator split = splits.get(id);
            if (split != null) {
                return split;
            }
            splits.put(id, split = new SplitAccumulatorImpl());
            return split;
        } finally {
            splitLock.set(false);
        }
    }
}
