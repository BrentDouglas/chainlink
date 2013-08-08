package io.machinecode.nock.jsl.api.partition;

import io.machinecode.nock.jsl.api.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Plan extends Strategy {

    String ELEMENT = "plan";

    String ONE = "1";

    String getPartitions();

    String getThreads();

    Properties getProperties();
}
