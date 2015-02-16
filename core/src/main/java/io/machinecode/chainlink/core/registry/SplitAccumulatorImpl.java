package io.machinecode.chainlink.core.registry;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import io.machinecode.chainlink.spi.registry.SplitAccumulator;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class SplitAccumulatorImpl implements SplitAccumulator {

    private long count = 0;
    private final TLongSet priorStepExecutionIds = new TLongHashSet();

    @Override
    public long incrementAndGetCallbackCount() {
        return ++count;
    }

    @Override
    public long[] getPriorStepExecutionIds() {
        return priorStepExecutionIds.toArray();
    }

    @Override
    public void addPriorStepExecutionId(final long priorStepExecutionId) {
        this.priorStepExecutionIds.add(priorStepExecutionId);
    }
}
