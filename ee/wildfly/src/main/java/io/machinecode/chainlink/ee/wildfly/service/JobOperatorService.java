package io.machinecode.chainlink.ee.wildfly.service;

import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.ee.wildfly.WildFlyEnvironment;
import io.machinecode.chainlink.ee.wildfly.configuration.Config;
import io.machinecode.chainlink.ee.wildfly.configuration.WildFlyConfiguration;
import io.machinecode.chainlink.ee.wildfly.configuration.WildFlyConfigurationDefaults;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

import javax.transaction.TransactionManager;
import java.lang.ref.WeakReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobOperatorService implements Service<ExtendedJobOperator> {

    private static final Logger log = Logger.getLogger(JobOperatorService.class);

    private final ServiceName module;
    private final WeakReference<ClassLoader> loader;

    private final Config config;
    private final String name;
    private JobOperatorImpl operator;

    private final InjectedValue<WildFlyEnvironment> environment = new InjectedValue<WildFlyEnvironment>();
    private final InjectedValue<TransactionManager> transactionManager = new InjectedValue<TransactionManager>();

    public JobOperatorService(final ServiceName module, final ClassLoader loader, final String name, final Config config) {
        this.module = module;
        this.loader = new WeakReference<ClassLoader>(loader);
        this.name = name;
        this.config = config;
    }

    @Override
    public void start(final StartContext context) throws StartException {
        try {
            operator = new JobOperatorImpl(new WildFlyConfiguration.Builder()
                    .setClassLoaderFactoryFqcn(config.classLoaderFactory)
                    .setWorkerFactoryFqcn(config.workerFactory)
                    .setExecutorFactoryFqcn(config.executorFactory)
                    .setRegistryFactoryFqcn(config.registryFactory)
                    .setMarshallingProviderFactoryFqcn(config.marshallingProviderFactory)
                    .setExecutionRepositoryFactoryFqcn(config.executionRepositoryFactory)
                    .setTransactionManagerFactoryFqcn(config.transactionManagerFactory)
                    .setMBeanServerFactoryFqcn(config.mBeanServerFactory)
                    .setJobLoaderFactoriesFqcns(config.jobLoaderFactories)
                    .setArtifactLoaderFactoriesFqcns(config.artifactLoaderFactories)
                    .setInjectorFactoriesFqcns(config.injectorFactories)
                    .setSecurityCheckFactoriesFqcns(config.securityCheckFactories)
                    .setConfigurationDefaults(new WildFlyConfigurationDefaults(
                            loader,
                            transactionManager.getValue())
                    )
                    .build()
            );
            environment.getValue().addOperator(module, name, loader.get(), operator);
        } catch (final Exception e) {
            throw new StartException(e);
        }
    }

    @Override
    public void stop(final StopContext context) {
        try {
            operator.close();
        } catch (final Exception e) {
            log.error("Error while stopping JobOperator", e); //TODO Message
        } finally {
            try {
                environment.getValue().removeOperator(module, name);
            } catch (final Exception e) {
                log.error("Error while removing JobOperator", e); //TODO Message
            } finally {
                operator = null;
            }
        }
    }

    @Override
    public ExtendedJobOperator getValue() throws IllegalStateException, IllegalArgumentException {
        return operator;
    }

    public InjectedValue<WildFlyEnvironment> getEnvironment() {
        return environment;
    }

    public InjectedValue<TransactionManager> getTransactionManager() {
        return transactionManager;
    }
}
