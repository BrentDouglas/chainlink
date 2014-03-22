package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.security.SecurityCheck;

import javax.transaction.TransactionManager;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Configuration {

    ClassLoader getClassLoader();

    ExecutionRepository getRepository();

    TransactionManager getTransactionManager();

    ExecutorFactory getExecutorFactory();

    String getProperty(final String key);

    Properties getProperties();

    JobLoader getJobLoader();

    ArtifactLoader getArtifactLoader();

    Injector getInjector();

    SecurityCheck getSecurityCheck();
}
