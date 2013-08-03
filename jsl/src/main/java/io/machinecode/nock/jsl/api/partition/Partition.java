package io.machinecode.nock.jsl.api.partition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Partition<T extends Strategy> {

    T getStrategy();

    Collector getCollector();

    Analyser getAnalyzer();

    Reducer getReducer();
}
