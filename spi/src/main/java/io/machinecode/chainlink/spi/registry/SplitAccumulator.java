package io.machinecode.chainlink.spi.registry;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public interface SplitAccumulator extends Accumulator {

    long[] getPriorStepExecutionIds();

    void addPriorStepExecutionId(final long priorStepExecutionId);
}
