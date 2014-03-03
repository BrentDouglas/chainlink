package io.machinecode.nock.tck.batch;

import io.machinecode.nock.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.nock.core.local.LocalExecutionRepository;
import io.machinecode.nock.core.local.LocalTransactionManager;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.configuration.ConfigurationFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchConfigurationFactory implements ConfigurationFactory {

    @Override
    public Configuration produce() {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setRepository(new LocalExecutionRepository())
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .build();
    }
}
