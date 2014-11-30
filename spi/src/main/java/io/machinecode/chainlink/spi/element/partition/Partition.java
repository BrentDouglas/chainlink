package io.machinecode.chainlink.spi.element.partition;

import io.machinecode.chainlink.spi.element.Element;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Partition<T extends Strategy> extends Element {

    String ELEMENT = "partition";

    T getStrategy();

    Collector getCollector();

    Analyser getAnalyzer();

    Reducer getReducer();
}
