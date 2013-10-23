package io.machinecode.nock.test.cdi.producer;

import io.machinecode.nock.test.core.transport.artifact.batchlet.RunBatchlet;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class RunBatchletProducer {
    @Produces
    @Named("run-batchlet")
    public javax.batch.api.Batchlet batchlet() {
        return new RunBatchlet();
    }
}
