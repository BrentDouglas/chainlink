package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.security.Security;
import io.machinecode.chainlink.spi.transport.Transport;

import javax.management.MBeanServer;
import javax.transaction.TransactionManager;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JobOperatorModel {

    Declaration<ClassLoader> getClassLoader();

    Declaration<Marshalling> getMarshalling();

    Declaration<Transport<?>> getTransport();

    Declaration<Registry> getRegistry();

    Declaration<ExecutionRepository> getExecutionRepository();

    Declaration<TransactionManager> getTransactionManager();

    Declaration<JobLoader> getJobLoader(final String name);

    Declaration<ArtifactLoader> getArtifactLoader(final String name);

    Declaration<Injector> getInjector(final String name);

    Declaration<Security> getSecurity(final String name);

    Declaration<Executor> getExecutor();

    Declaration<MBeanServer> getMBeanServer();

    Properties getProperties();
}
