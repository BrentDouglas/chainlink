package io.mackinecode.nock.test.spring;

import io.machinecode.nock.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.nock.inject.spring.SpringArtifactLoader;
import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.test.core.transport.LocalTransportTest;
import org.junit.BeforeClass;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SpringLocalTransportTest extends LocalTransportTest {

    private static AbstractApplicationContext context;

    @Override
    protected Builder _configuration() {
        return super._configuration()
                .setArtifactLoaders(new ArtifactLoader[]{context.getBean(SpringArtifactLoader.class)});
    }

    @BeforeClass
    public static void beforeClass() {
        context = new ClassPathXmlApplicationContext("beans.xml");
    }
}
