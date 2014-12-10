package io.machinecode.chainlink.ee.wildfly.configuration;

import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.management.jmx.PlatformMBeanServerFactory;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.marshalling.jboss.JbossMarshallingFactory;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ClassLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;

import javax.transaction.TransactionManager;
import java.lang.ref.WeakReference;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class WildFlyConfigurationDefaults implements JobOperatorConfiguration {

    private final TransactionManager transactionManager;
    private final WeakReference<ClassLoader> loader;

    public WildFlyConfigurationDefaults(final WeakReference<ClassLoader> loader, final TransactionManager transactionManager) {
        this.loader = loader;
        this.transactionManager = transactionManager;
    }

    @Override
    public void configureJobOperator(final JobOperatorModel model) throws Exception {
        model.getClassLoader().setDefaultValueFactory(new ClassLoaderFactory() {
            @Override
            public ClassLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return loader.get();
            }
        });
        model.getTransactionManager().setDefaultValueFactory(new TransactionManagerFactory() {
            @Override
            public TransactionManager produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return transactionManager;
            }
        });
        model.getExecutionRepository().setDefaultValueFactory(new MemoryExecutionRepositoryFactory());
        model.getMarshalling().setDefaultValueFactory(new JbossMarshallingFactory());
        model.getMBeanServer().setDefaultValueFactory(new PlatformMBeanServerFactory());
        model.getTransport().setDefaultValueFactory(new LocalTransportFactory());
        model.getRegistry().setDefaultValueFactory(new LocalRegistryFactory());
        model.getExecutor().setDefaultValueFactory(new EventedExecutorFactory());
    }
}
