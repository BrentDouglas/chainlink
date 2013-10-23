package io.machinecode.nock.test.cdi;

import io.machinecode.nock.cdi.CdiArtifactLoader;
import io.machinecode.nock.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.test.core.transport.TransportTest;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CdiLocalTransportTest extends TransportTest {

    private static Weld weld;
    private static WeldContainer container;

    @Override
    protected Builder _configuration() {
        return super._configuration()
                .setArtifactLoaders(new ArtifactLoader[]{ CdiArtifactLoader.inject(container.getBeanManager(), CdiArtifactLoader.class) });
    }

    @BeforeClass
    public static void beforeClass() {
        weld = new Weld();
        container = weld.initialize();
    }

    @AfterClass
    public static void afterClass() {
        weld.shutdown();
    }

}
