package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.spi.security.Security;
import io.machinecode.chainlink.spi.transport.Transport;

import javax.management.MBeanServer;
import javax.transaction.TransactionManager;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
class DummyConfiguration implements Configuration {
    @Override
    public io.machinecode.chainlink.spi.execution.Executor getExecutor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TransactionManager getTransactionManager() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Marshalling getMarshalling() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MBeanServer getMBeanServer() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JobLoader getJobLoader() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArtifactLoader getArtifactLoader() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Security getSecurity() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Repository getRepository() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Transport getTransport() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Registry getRegistry() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InjectionContext getInjectionContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getProperty(final String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getProperty(final String name, final String defaultValue) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
