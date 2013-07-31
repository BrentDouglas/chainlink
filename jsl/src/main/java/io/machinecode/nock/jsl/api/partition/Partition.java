package io.machinecode.nock.jsl.api.partition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Partition<T extends Mapper> {

    T getMapper();

    Collector getCollector();

    Analyser getAnalyzer();

    PartitionReducer getReducer();
}
