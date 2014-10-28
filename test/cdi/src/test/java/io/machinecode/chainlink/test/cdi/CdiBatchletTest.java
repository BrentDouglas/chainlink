package io.machinecode.chainlink.test.cdi;

import io.machinecode.chainlink.se.configuration.SeConfiguration.Builder;
import io.machinecode.chainlink.core.element.JobImpl;
import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.inject.cdi.CdiArtifactLoader;
import io.machinecode.chainlink.inject.cdi.CdiInjector;
import io.machinecode.chainlink.jsl.fluent.Jsl;
import io.machinecode.chainlink.marshalling.jdk.JdkMarshaller;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.batchlet.BatchletTest;
import io.machinecode.chainlink.test.core.execution.batchlet.artifact.InjectedBatchlet;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class CdiBatchletTest extends BatchletTest {

    private static Weld weld;
    private static WeldContainer container;

    @Override
    protected Builder _configuration() throws Exception{
        return super._configuration()
                .setArtifactLoaders(CdiArtifactLoader.inject(container.getBeanManager(), CdiArtifactLoader.class))
                .setInjectors(CdiArtifactLoader.inject(container.getBeanManager(), CdiInjector.class));
    }
    @Override
    protected ExecutionRepository _repository() {
        return new MemoryExecutionRepository(new JdkMarshaller());
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
