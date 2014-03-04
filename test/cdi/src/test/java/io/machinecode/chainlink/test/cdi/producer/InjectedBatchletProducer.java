package io.machinecode.chainlink.test.cdi.producer;

import io.machinecode.chainlink.test.core.execution.artifact.batchlet.InjectedBatchlet;

import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class InjectedBatchletProducer {
    @Produces
    @Named("injectedBatchlet")
    public javax.batch.api.Batchlet batchlet(@New InjectedBatchlet batchlet) {
        return batchlet;
    }

    @Produces
    @Named("unmanagedInjectedBatchlet")
    public javax.batch.api.Batchlet unmanaged() {
        return new InjectedBatchlet();
    }
}
