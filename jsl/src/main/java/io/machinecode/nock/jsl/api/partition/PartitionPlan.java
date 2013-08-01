package io.machinecode.nock.jsl.api.partition;

import io.machinecode.nock.jsl.api.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PartitionPlan extends Mapper {

    int getPartitions();

    Integer getThreads();

    Properties getProperties();
}
