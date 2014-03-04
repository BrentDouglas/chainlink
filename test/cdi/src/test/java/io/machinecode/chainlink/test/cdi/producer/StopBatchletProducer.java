package io.machinecode.chainlink.test.cdi.producer;

import io.machinecode.chainlink.test.core.execution.artifact.batchlet.StopBatchlet;

import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class StopBatchletProducer {
    @Produces
    @Named("stopBatchlet")
    public javax.batch.api.Batchlet batchlet(@New StopBatchlet batchlet) {
        return batchlet;
    }
}
