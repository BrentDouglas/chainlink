package io.machinecode.chainlink.test.cdi.producer;

import io.machinecode.chainlink.core.execution.batchlet.artifact.FailBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.InjectedBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.RunBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.StopBatchlet;

import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
*/
public class BatchletProducer {

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

    @Produces
    @Named("failBatchlet")
    public javax.batch.api.Batchlet batchlet(@New FailBatchlet batchlet) {
        return batchlet;
    }

    @Produces
    @Named("stopBatchlet")
    public javax.batch.api.Batchlet batchlet(@New StopBatchlet batchlet) {
        return batchlet;
    }

    @Produces
    @Named("runBatchlet")
    public javax.batch.api.Batchlet batchlet(@New RunBatchlet batchlet) {
        return batchlet;
    }
}
