package io.machinecode.nock.tck.batch;

import io.machinecode.nock.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.nock.core.local.LocalRepository;
import io.machinecode.nock.core.local.LocalTransactionManager;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.configuration.ConfigurationFactory;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchConfigurationFactory implements ConfigurationFactory {

    @Override
    public Configuration produce() {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setRepository(new LocalRepository())
                .setTransactionManager(new LocalTransactionManager(180))
                .build();
    }
}
