package io.machinecode.chainlink.se;

import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.transaction.LocalTransactionManagerFactory;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.marshalling.jdk.JdkMarshallingFactory;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ClassLoaderFactory;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class SeConfigurationDefaults implements JobOperatorConfiguration {

    final ClassLoader tccl = Thread.currentThread().getContextClassLoader();

    @Override
    public void configureJobOperator(final JobOperatorModel model) throws Exception {
        model.getClassLoader().setDefaultValueFactory(new ClassLoaderFactory() {
            @Override
            public ClassLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return tccl;
            }
        });
        model.getTransactionManager().setDefaultValueFactory(new LocalTransactionManagerFactory(180, TimeUnit.SECONDS));
        model.getExecutionRepository().setDefaultValueFactory(new MemoryExecutionRepositoryFactory());
        model.getMarshalling().setDefaultValueFactory(new JdkMarshallingFactory());
        model.getTransport().setDefaultValueFactory(new LocalTransportFactory());
        model.getRegistry().setDefaultValueFactory(new LocalRegistryFactory());
        model.getExecutor().setDefaultValueFactory(new EventedExecutorFactory());
    }
}
