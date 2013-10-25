package io.machinecode.nock.spi.element.partition;

import io.machinecode.nock.spi.PropertiesElement;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Plan extends Strategy, PropertiesElement {

    String ELEMENT = "plan";

    String ONE = "1";

    String getPartitions();

    String getThreads();
}
