package io.machinecode.chainlink.ee.wildfly.configuration;

import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.management.jmx.PlatformMBeanServerFactory;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.transaction.ReferenceTransactionManagerFactory;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.marshalling.jboss.JbossMarshallingFactory;
import io.machinecode.chainlink.core.repository.memory.MemoryExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ClassLoaderFactory;

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
        model.getClassLoader().setDefaultFactory(new ClassLoaderFactory() {
            @Override
            public ClassLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return loader.get();
            }
        });
        model.getTransactionManager().setDefaultFactory(new ReferenceTransactionManagerFactory(transactionManager));
        model.getExecutionRepository().setDefaultFactory(new MemoryExecutionRepositoryFactory());
        model.getMarshalling().setDefaultFactory(new JbossMarshallingFactory());
        model.getMBeanServer().setDefaultFactory(new PlatformMBeanServerFactory());
        model.getTransport().setDefaultFactory(new LocalTransportFactory());
        model.getRegistry().setDefaultFactory(new LocalRegistryFactory());
        model.getExecutor().setDefaultFactory(new EventedExecutorFactory());
    }
}
