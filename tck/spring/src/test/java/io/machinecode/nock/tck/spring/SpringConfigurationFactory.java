package io.machinecode.nock.tck.spring;

import io.machinecode.nock.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.nock.core.local.LocalExecutionRepository;
import io.machinecode.nock.core.local.LocalTransactionManager;
import io.machinecode.nock.inject.spring.SpringArtifactLoader;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.configuration.ConfigurationFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SpringConfigurationFactory implements ConfigurationFactory {

    private static AbstractApplicationContext context;

    static {
        context = new ClassPathXmlApplicationContext("beans.xml");
    }

    @Override
    public Configuration produce() {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setRepository(new LocalExecutionRepository())
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .setArtifactLoaders(context.getBean(SpringArtifactLoader.class))
                .build();
    }
}
