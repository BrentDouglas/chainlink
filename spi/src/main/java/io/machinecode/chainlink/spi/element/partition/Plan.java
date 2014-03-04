package io.machinecode.chainlink.spi.element.partition;

import io.machinecode.chainlink.spi.element.Properties;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Plan extends Strategy {

    String ELEMENT = "plan";

    String ONE = "1";

    String getPartitions();

    String getThreads();

    List<? extends Properties> getProperties();
}
