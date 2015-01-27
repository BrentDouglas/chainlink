package io.machinecode.chainlink.spi.jsl.partition;

import io.machinecode.chainlink.spi.jsl.Properties;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Plan extends Strategy {

    String ELEMENT = "plan";

    String ONE = "1";

    String getPartitions();

    String getThreads();

    List<? extends Properties> getProperties();
}
