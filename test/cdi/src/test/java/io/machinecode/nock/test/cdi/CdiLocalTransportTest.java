package io.machinecode.nock.test.cdi;

import io.machinecode.nock.cdi.CdiExtension;
import io.machinecode.nock.core.configuration.RuntimeConfigurationImpl;
import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.test.core.transport.LocalTransportTest;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CdiLocalTransportTest extends LocalTransportTest {

    private static Weld weld;
    private static WeldContainer container;

    @BeforeClass
    public static void beforeClass() {
        weld = new Weld();
        container = weld.initialize();
        configuration = new RuntimeConfigurationImpl(configuration()
                .setArtifactLoaders(new ArtifactLoader[]{ CdiExtension.inject(container.getBeanManager(), CdiExtension.class) })
                .build());
    }

    @AfterClass
    public static void afterClass() {
        weld.shutdown();
    }

}
