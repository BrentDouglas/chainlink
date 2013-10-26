package io.machinecode.nock.test.cdi;

import io.machinecode.nock.cdi.CdiArtifactLoader;
import io.machinecode.nock.cdi.CdiInjector;
import io.machinecode.nock.core.JobOperatorImpl;
import io.machinecode.nock.core.JobOperatorImpl.Start;
import io.machinecode.nock.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.nock.core.factory.JobFactory;
import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.jsl.fluent.Jsl;
import io.machinecode.nock.test.core.transport.TransportTest;
import io.machinecode.nock.test.core.transport.artifact.batchlet.InjectedBatchlet;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CdiLocalTransportTest extends TransportTest {

    private static Weld weld;
    private static WeldContainer container;

    @Override
    protected Builder _configuration() {
        return super._configuration()
                .setArtifactLoaders(CdiArtifactLoader.inject(container.getBeanManager(), CdiArtifactLoader.class))
                .setInjectors(CdiArtifactLoader.inject(container.getBeanManager(), CdiInjector.class));
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
        final JobImpl job = JobFactory.INSTANCE.produceExecution(Jsl.job()
                .setId("job")
                .addExecution(
                        Jsl.stepWithBatchletAndPlan()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("unmanaged-injected-batchlet")
                                                .addProperty("property", "value")
                                )
                ), PARAMETERS);
        final JobOperatorImpl operator = new JobOperatorImpl(configuration(), transport());
        final Start start = operator.start(job);
        final JobExecution execution = repository().getJobExecution(start.id);
        Assert.assertEquals("Batch Status", BatchStatus.STARTED, execution.getBatchStatus()); //TODO Race
        Assert.assertEquals("Exit  Status", BatchStatus.STARTED.name(), execution.getExitStatus());
        start.deferred.get();
        Assert.assertTrue(InjectedBatchlet.hasRun.get());
        Assert.assertEquals("Batch Status", BatchStatus.COMPLETED, repository().getJobExecution(execution.getExecutionId()).getBatchStatus());
        Assert.assertEquals("Exit  Status", BatchStatus.COMPLETED.name(), repository().getJobExecution(execution.getExecutionId()).getExitStatus());
    }
}
