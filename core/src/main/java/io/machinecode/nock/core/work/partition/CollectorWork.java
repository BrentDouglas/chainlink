package io.machinecode.nock.core.work.partition;

import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.Work;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.partition.Collector;

import javax.batch.api.partition.PartitionCollector;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CollectorWork implements Work, Collector {

    private final ResolvableReference<PartitionCollector> collector;

    public CollectorWork(final String ref) {
        this.collector = new ResolvableReference<PartitionCollector>(ref, PartitionCollector.class);
    }

    @Override
    public String getRef() {
        return this.collector.ref();
    }

    @Override
    public Properties getProperties() {
        return null;
    }
}
