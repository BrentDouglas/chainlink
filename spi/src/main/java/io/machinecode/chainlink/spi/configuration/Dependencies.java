package io.machinecode.chainlink.spi.configuration;

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
public interface Dependencies {

    ClassLoader getClassLoader();

    TransactionManager getTransactionManager();

    Marshalling getMarshalling();

    MBeanServer getMBeanServer();

    JobLoader getJobLoader();

    ArtifactLoader getArtifactLoader();

    Security getSecurity();

    Repository getRepository();

    Transport getTransport();

    Registry getRegistry();

    InjectionContext getInjectionContext();
}
