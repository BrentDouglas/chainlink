package io.machinecode.nock.test.core.execution;

import io.machinecode.nock.core.factory.JobFactory;
import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.jsl.fluent.Jsl;
import io.machinecode.nock.spi.ExtendedJobInstance;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import java.util.Date;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RepositoryTest extends BaseTest {

    @Test
    public void runRepositoryTest() throws Exception {
        printMethodName();
        final JobImpl job = JobFactory.INSTANCE.produceExecution(Jsl.job()
                .setId("job")
                .addExecution(
                        Jsl.stepWithBatchletAndPlan()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("runBatchlet")
                                )
                ), PARAMETERS);
        final ExtendedJobInstance instance = repository().createJobInstance(job, "job");
        final JobExecution execution = repository().createJobExecution(instance, PARAMETERS, new Date());
        Assert.assertEquals(BatchStatus.STARTING, execution.getBatchStatus());
        Assert.assertEquals(BatchStatus.STARTING.name(), execution.getExitStatus());
    }
}
