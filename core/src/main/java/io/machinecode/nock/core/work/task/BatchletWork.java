package io.machinecode.nock.core.work.task;

import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.Work;
import io.machinecode.nock.spi.element.Properties;

import javax.batch.api.Batchlet;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletWork implements Work, io.machinecode.nock.spi.element.task.Batchlet {

    private final ResolvableReference<Batchlet> batchlet;

    public BatchletWork(final String ref) {
        this.batchlet = new ResolvableReference<javax.batch.api.Batchlet>(ref, javax.batch.api.Batchlet.class);
    }

    @Override
    public String getRef() {
        return this.batchlet.ref();
    }

    @Override
    public Properties getProperties() {
        return null;
    }
}
