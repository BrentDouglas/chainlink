package io.machinecode.chainlink.test.cdi.producer;

import io.machinecode.chainlink.test.core.execution.artifact.batchlet.RunBatchlet;

import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class RunBatchletProducer {
    @Produces
    @Named("runBatchlet")
    public javax.batch.api.Batchlet batchlet(@New RunBatchlet batchlet) {
        return batchlet;
    }
}
