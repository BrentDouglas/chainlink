package io.machinecode.nock.spi;

import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PartitionExecution extends BaseExecution {

    long getStepExecutionId();

    int getPartitionId();

    Properties getPartitionProperties();
}
