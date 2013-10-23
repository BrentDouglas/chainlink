package io.machinecode.nock.test.cdi.producer;

import io.machinecode.nock.test.core.transport.artifact.batchlet.StopBatchlet;

import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class StopBatchletProducer {
    @Produces
    @Named("stop-batchlet")
    public javax.batch.api.Batchlet batchlet(@New StopBatchlet batchlet) {
        return batchlet;
    }
}
