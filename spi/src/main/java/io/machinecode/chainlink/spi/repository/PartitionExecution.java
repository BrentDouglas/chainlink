package io.machinecode.chainlink.spi.repository;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface PartitionExecution extends BaseExecution {

    long getPartitionExecutionId();

    long getStepExecutionId();

    int getPartitionId();

    Properties getPartitionParameters();
}
