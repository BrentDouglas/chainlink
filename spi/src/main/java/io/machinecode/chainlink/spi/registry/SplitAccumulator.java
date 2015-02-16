package io.machinecode.chainlink.spi.registry;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public interface SplitAccumulator {

    long incrementAndGetCallbackCount();

    long[] getPriorStepExecutionIds();

    void addPriorStepExecutionId(final long priorStepExecutionId);
}
