package io.machinecode.chainlink.spi.element.partition;

import io.machinecode.chainlink.spi.element.Properties;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Plan extends Strategy {

    String ELEMENT = "plan";

    String ONE = "1";

    String getPartitions();

    String getThreads();

    List<? extends Properties> getProperties();
}
