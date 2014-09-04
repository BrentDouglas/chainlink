package io.machinecode.chainlink.spi.registry;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public interface SplitAccumulator extends Accumulator {

    long[] getPriorStepExecutionIds();

    void addPriorStepExecutionId(final long priorStepExecutionId);
}
