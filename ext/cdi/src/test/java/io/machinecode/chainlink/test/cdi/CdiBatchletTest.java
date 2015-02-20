package io.machinecode.chainlink.test.cdi;

import io.machinecode.chainlink.core.jsl.impl.JobImpl;
import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.inject.cdi.CdiArtifactLoader;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.core.execution.batchlet.BatchletTest;
import io.machinecode.chainlink.core.execution.batchlet.artifact.InjectedBatchlet;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CdiBatchletTest extends BatchletTest {

    private static Weld weld;
    private static WeldContainer container;

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        super.visitJobOperatorModel(model);
        model.getArtifactLoader("artifactLoader").setValue(new CdiArtifactLoader(container.getBeanManager()));
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


    @Test
    public void unmanagedInjectedBatchletTest() throws Exception {
        printMethodName();
        final JobImpl job = JobFactory.produce(Jsl.job("unmanaged-injected-job")
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.batchlet("unmanagedInjectedBatchlet")
                                                .addProperty("property", "value")
                                )
                ), PARAMETERS);
        final JobOperationImpl operation = operator.startJob(job, "unmanaged-injected-job", PARAMETERS);
        operation.get();
        Assert.assertTrue(InjectedBatchlet.hasRun.get());
        assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }
}
