package io.machinecode.chainlink.spi.jsl.partition;

import io.machinecode.chainlink.spi.jsl.Element;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Partition<T extends Strategy> extends Element {

    String ELEMENT = "partition";

    T getStrategy();

    Collector getCollector();

    Analyser getAnalyzer();

    Reducer getReducer();
}
