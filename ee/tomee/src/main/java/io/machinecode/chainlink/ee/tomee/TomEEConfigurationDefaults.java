package io.machinecode.chainlink.ee.tomee;

import io.machinecode.chainlink.core.configuration.ClassLoaderFactoryImpl;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.management.jmx.PlatformMBeanServerFactory;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.core.marshalling.JdkMarshallingFactory;
import io.machinecode.chainlink.core.repository.memory.MemoryExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class TomEEConfigurationDefaults implements JobOperatorConfiguration {

    final ClassLoader loader;

    TomEEConfigurationDefaults(final ClassLoader loader) {
        this.loader = loader;
    }

    @Override
    public void configureJobOperator(final JobOperatorModel model) throws Exception {
        model.getClassLoader().setDefaultFactory(new ClassLoaderFactoryImpl(loader));
        model.getTransactionManager().setDefaultFactory(new TomEETransactionManagerFactory());
        model.getExecutionRepository().setDefaultFactory(new MemoryExecutionRepositoryFactory());
        model.getMarshalling().setDefaultFactory(new JdkMarshallingFactory());
        model.getMBeanServer().setDefaultFactory(new PlatformMBeanServerFactory());
        model.getTransport().setDefaultFactory(new LocalTransportFactory());
        model.getRegistry().setDefaultFactory(new LocalRegistryFactory());
        model.getExecutor().setDefaultFactory(new EventedExecutorFactory());
    }

}
