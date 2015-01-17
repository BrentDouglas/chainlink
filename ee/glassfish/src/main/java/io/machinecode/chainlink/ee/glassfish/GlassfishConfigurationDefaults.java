package io.machinecode.chainlink.ee.glassfish;

import io.machinecode.chainlink.core.configuration.ClassLoaderFactoryImpl;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.execution.ThreadFactoryLookup;
import io.machinecode.chainlink.core.management.jmx.PlatformMBeanServerFactory;
import io.machinecode.chainlink.core.marshalling.JdkMarshallingFactory;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.repository.memory.MemoryExecutionRepositoryFactory;
import io.machinecode.chainlink.core.transaction.JndiTransactionManagerFactory;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class GlassfishConfigurationDefaults implements JobOperatorConfiguration {

    final ClassLoader loader;
    final ThreadFactoryLookup threadFactory;

    GlassfishConfigurationDefaults(final ClassLoader loader, final ThreadFactoryLookup threadFactory) {
        this.loader = loader;
        this.threadFactory = threadFactory;
    }

    @Override
    public void configureJobOperator(final JobOperatorModel model) throws Exception {
        model.getClassLoader().setDefaultFactory(new ClassLoaderFactoryImpl(loader));
        model.getTransactionManager().setDefaultFactory(new JndiTransactionManagerFactory("java:appserver/TransactionManager"));
        model.getExecutionRepository().setDefaultFactory(new MemoryExecutionRepositoryFactory());
        model.getMarshalling().setDefaultFactory(new JdkMarshallingFactory());
        model.getMBeanServer().setDefaultFactory(new PlatformMBeanServerFactory());
        model.getTransport().setDefaultFactory(new LocalTransportFactory());
        model.getRegistry().setDefaultFactory(new LocalRegistryFactory());
        model.getExecutor().setDefaultFactory(new EventedExecutorFactory(this.threadFactory));
    }
}
