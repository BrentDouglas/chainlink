package io.machinecode.chainlink.spi.repository;

import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PartitionExecution extends BaseExecution {

    long getPartitionExecutionId();

    long getStepExecutionId();

    int getPartitionId();

    Properties getPartitionParameters();
}
