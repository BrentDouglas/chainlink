package io.machinecode.chainlink.spi.repository;

import javax.batch.runtime.StepExecution;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ExtendedStepExecution extends StepExecution, BaseExecution {

    long getJobExecutionId();
}
