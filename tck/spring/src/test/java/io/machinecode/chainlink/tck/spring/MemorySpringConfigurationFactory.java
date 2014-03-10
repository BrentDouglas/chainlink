package io.machinecode.chainlink.tck.spring;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.inject.spring.SpringArtifactLoader;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MemorySpringConfigurationFactory implements ConfigurationFactory {

    private static AbstractApplicationContext context;

    static {
        context = new ClassPathXmlApplicationContext("beans.xml");
    }

    @Override
    public Configuration produce() {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setRepository(new MemoryExecutionRepository())
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .setArtifactLoaders(context.getBean(SpringArtifactLoader.class))
                .build();
    }
}
