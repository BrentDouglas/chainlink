package io.machinecode.chainlink.test.seam;

import io.machinecode.chainlink.core.inject.VetoInjector;
import io.machinecode.chainlink.inject.seam.SeamArtifactLoader;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.test.core.execution.batchlet.BatchletTest;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.mock.MockServletContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SeamBatchletTest extends BatchletTest {

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getArtifactLoader("artifactFactory").set(SeamArtifactLoader.inject("seamArtifactLoader", SeamArtifactLoader.class));
        model.getInjector("injector").set(new VetoInjector());
    }

    @BeforeClass
    public static void beforeClass() {
        final ServletContext context = new MockServletContext();
        ServletLifecycle.beginApplication(context);
        new Initialization(context).create().init();
    }

    @AfterClass
    public static void AfterClass() {
        Lifecycle.endApplication();
    }
}
