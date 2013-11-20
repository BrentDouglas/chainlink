package io.machinecode.nock.test.cdi.producer;

import io.machinecode.nock.test.core.execution.artifact.batchlet.InjectedBatchlet;

import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class InjectedBatchletProducer {
    @Produces
    @Named("injected-batchlet")
    public javax.batch.api.Batchlet batchlet(@New InjectedBatchlet batchlet) {
        return batchlet;
    }

    @Produces
    @Named("unmanaged-injected-batchlet")
    public javax.batch.api.Batchlet unmanaged() {
        return new InjectedBatchlet();
    }
}
