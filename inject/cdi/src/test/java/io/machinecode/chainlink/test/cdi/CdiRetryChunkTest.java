package io.machinecode.chainlink.test.cdi;

import io.machinecode.chainlink.inject.cdi.CdiArtifactLoader;
import io.machinecode.chainlink.inject.cdi.CdiInjector;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.core.execution.chunk.RetryChunkTest;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CdiRetryChunkTest extends RetryChunkTest {

    private static Weld weld;
    private static WeldContainer container;

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        super.visitJobOperatorModel(model);
        model.getArtifactLoader("artifactLoader").setValue(CdiArtifactLoader.inject(container.getBeanManager(), CdiArtifactLoader.class));
        model.getInjector("injector").setValue(CdiArtifactLoader.inject(container.getBeanManager(), CdiInjector.class));
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
