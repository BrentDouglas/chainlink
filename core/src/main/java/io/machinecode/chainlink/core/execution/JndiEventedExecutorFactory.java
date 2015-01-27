package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.execution.Executor;

import javax.naming.InitialContext;
import java.util.Properties;
import java.util.concurrent.ThreadFactory;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JndiEventedExecutorFactory implements ExecutorFactory {

    @Override
    public Executor produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new EventedExecutor(dependencies, properties, InitialContext.<ThreadFactory>doLookup(properties.getProperty(Constants.THREAD_FACTORY_JNDI_NAME)));
    }
}
