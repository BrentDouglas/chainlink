package io.machinecode.nock.jsl.api.partition;

import io.machinecode.nock.jsl.api.Element;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Partition<T extends Strategy> extends Element {

    String ELEMENT = "partition";

    T getStrategy();

    Collector getCollector();

    Analyser getAnalyzer();

    Reducer getReducer();
}
