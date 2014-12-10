package io.machinecode.chainlink.ee.glassfish;

import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.management.jmx.PlatformMBeanServerFactory;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.marshalling.jdk.JdkMarshallingFactory;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ClassLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;

import javax.transaction.TransactionManager;
import java.util.Properties;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class GlassfishConfigurationDefaults implements JobOperatorConfiguration {

    final ClassLoader loader;
    final TransactionManager transactionManager;

    GlassfishConfigurationDefaults(final ClassLoader loader, final TransactionManager transactionManager) {
        this.loader = loader;
        this.transactionManager = transactionManager;
    }

    @Override
    public void configureJobOperator(final JobOperatorModel model) throws Exception {
        model.getClassLoader().setDefaultValueFactory(new ClassLoaderFactory() {
            @Override
            public ClassLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return loader;
            }
        });
        model.getTransactionManager().setDefaultValueFactory(new TransactionManagerFactory() {
            @Override
            public TransactionManager produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return transactionManager;
            }
        });
        model.getExecutionRepository().setDefaultValueFactory(new MemoryExecutionRepositoryFactory());
        model.getMarshalling().setDefaultValueFactory(new JdkMarshallingFactory());
        model.getMBeanServer().setDefaultValueFactory(new PlatformMBeanServerFactory());
        model.getTransport().setDefaultValueFactory(new LocalTransportFactory());
        model.getRegistry().setDefaultValueFactory(new LocalRegistryFactory());
        model.getExecutor().setDefaultValueFactory(new EventedExecutorFactory());
    }
}
