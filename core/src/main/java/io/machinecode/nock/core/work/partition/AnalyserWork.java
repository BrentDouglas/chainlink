package io.machinecode.nock.core.work.partition;

import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.Work;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.partition.Analyser;

import javax.batch.api.partition.PartitionAnalyzer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AnalyserWork implements Work, Analyser {

    private final ResolvableReference<PartitionAnalyzer> analyser;

    public AnalyserWork(final ResolvableReference<PartitionAnalyzer> analyser) {
        this.analyser = analyser;
    }

    @Override
    public String getRef() {
        return this.analyser.ref();
    }

    @Override
    public Properties getProperties() {
        return null;
    }
}
