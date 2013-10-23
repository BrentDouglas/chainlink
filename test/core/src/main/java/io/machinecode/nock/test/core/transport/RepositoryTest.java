package io.machinecode.nock.test.core.transport;

import io.machinecode.nock.core.factory.JobFactory;
import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.jsl.fluent.Jsl;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RepositoryTest extends BaseTest {

    @Test
    public void runRepositoryTest() throws Exception {
        final JobImpl job = JobFactory.INSTANCE.produceExecution(Jsl.job()
                .setId("job")
                .addExecution(
                        Jsl.stepWithBatchletAndPlan()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("run-batchlet")
                                )
                ), PARAMETERS);
        final JobInstance instance = repository().createJobInstance(job);
        final JobExecution execution = repository().createJobExecution(instance);
        Assert.assertEquals(BatchStatus.STARTING, execution.getBatchStatus());
        Assert.assertEquals(BatchStatus.STARTING.name(), execution.getExitStatus());
    }
}
