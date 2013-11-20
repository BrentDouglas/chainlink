package io.machinecode.nock.test.cdi.producer;

import io.machinecode.nock.test.core.execution.artifact.batchlet.FailBatchlet;

import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class FailBatchletProducer {
    @Produces
    @Named("fail-batchlet")
    public javax.batch.api.Batchlet batchlet(@New FailBatchlet batchlet) {
        return batchlet;
    }
}
