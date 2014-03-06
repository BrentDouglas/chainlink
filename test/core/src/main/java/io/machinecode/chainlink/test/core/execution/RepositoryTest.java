package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.repository.core.MetricImpl;
import io.machinecode.chainlink.core.element.JobImpl;
import io.machinecode.chainlink.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.spi.element.execution.Step;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import java.util.Date;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class RepositoryTest extends BaseTest {

    private JobImpl _job() {
        return JobFactory.INSTANCE.produceExecution(Jsl.job()
                .setId("job")
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("runBatchlet")
                                )
                ), PARAMETERS);
    }

    @Test
    public void createJobInstanceTest() throws Exception {
        printMethodName();

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job, "jsl");
        Assert.assertEquals("job", jobInstance.getJobName());
        Assert.assertEquals("jsl", jobInstance.getJslName());

        final ExtendedJobInstance second = repository().createJobInstance(job, "jsl");

        Assert.assertNotSame(jobInstance.getInstanceId(), second.getInstanceId());
    }

    @Test
    public void createJobExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job, "jsl");
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );

        Assert.assertEquals("job", jobExecution.getJobName());
        Assert.assertEquals(BatchStatus.STARTING, jobExecution.getBatchStatus());
        Assert.assertNull(jobExecution.getExitStatus());
        Assert.assertNotNull(jobExecution.getCreateTime());
        Assert.assertNotNull(jobExecution.getLastUpdatedTime());
        Assert.assertNull(jobExecution.getStartTime());
        Assert.assertNull(jobExecution.getEndTime());
        Assert.assertNull(jobExecution.getRestartElementId());
        final Properties params = jobExecution.getJobParameters();
        Assert.assertNotNull(params);
        Assert.assertEquals("value", params.getProperty("test"));
        Assert.assertEquals(1, params.stringPropertyNames().size());

        final ExtendedJobExecution second = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );

        Assert.assertNotSame(jobExecution.getExecutionId(), second.getExecutionId());
    }

    @Test
    public void createStepExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job, "jsl");
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        final ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)),
                new Date()
        );

        Assert.assertEquals("step", stepExecution.getStepName());
        Assert.assertEquals(BatchStatus.STARTING, stepExecution.getBatchStatus());
        Assert.assertNull(stepExecution.getExitStatus());
        Assert.assertNotNull(stepExecution.getCreateTime());
        Assert.assertNotNull(stepExecution.getUpdatedTime());
        Assert.assertNull(stepExecution.getStartTime());
        Assert.assertNull(stepExecution.getEndTime());
        Assert.assertNull(stepExecution.getPersistentUserData());
        Assert.assertNull(stepExecution.getReaderCheckpoint());
        Assert.assertNull(stepExecution.getWriterCheckpoint());

        _testEmptyMetrics(stepExecution.getMetrics());

        final ExtendedStepExecution second = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)),
                new Date()
        );

        Assert.assertNotSame(stepExecution.getStepExecutionId(), second.getStepExecutionId());
    }

    @Test
    public void createPartitionExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job, "jsl");
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        final ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)),
                new Date()
        );

        final PartitionExecution partitionExecution = repository().createPartitionExecution(
                stepExecution.getStepExecutionId(),
                4,
                parameters,
                new Date()
        );

        Assert.assertEquals(stepExecution.getStepExecutionId(), partitionExecution.getStepExecutionId());
        Assert.assertEquals(4, partitionExecution.getPartitionId());
        Assert.assertEquals(BatchStatus.STARTING, partitionExecution.getBatchStatus());
        Assert.assertNull(partitionExecution.getExitStatus());
        Assert.assertNotNull(partitionExecution.getCreateTime());
        Assert.assertNotNull(partitionExecution.getUpdatedTime());
        Assert.assertNull(partitionExecution.getStartTime());
        Assert.assertNull(partitionExecution.getEndTime());
        Assert.assertNull(partitionExecution.getPersistentUserData());
        Assert.assertNull(partitionExecution.getReaderCheckpoint());
        Assert.assertNull(partitionExecution.getWriterCheckpoint());

        _testEmptyMetrics(partitionExecution.getMetrics());

        final Properties params = partitionExecution.getPartitionParameters();
        Assert.assertNotNull(params);
        Assert.assertEquals("value", params.getProperty("test"));
        Assert.assertEquals(1, params.stringPropertyNames().size());
    }

    @Test
    public void otherCreatePartitionExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job, "jsl");
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        final ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)),
                new Date()
        );

        PartitionExecution original = repository().createPartitionExecution(
                stepExecution.getStepExecutionId(),
                4,
                parameters,
                new Date()
        );

        repository().updatePartitionExecution(
                original.getPartitionExecutionId(),
                original.getMetrics(),
                "persistent",
                "reader",
                "writer",
                new Date()
        );
        original = repository().getPartitionExecution(original.getPartitionExecutionId());

        final PartitionExecution partitionExecution = repository().createPartitionExecution(
                stepExecution.getStepExecutionId(),
                repository().getPartitionExecution(original.getPartitionExecutionId()),
                new Date()
        );

        Assert.assertEquals(stepExecution.getStepExecutionId(), partitionExecution.getStepExecutionId());
        Assert.assertEquals(4, partitionExecution.getPartitionId());
        Assert.assertEquals(BatchStatus.STARTING, partitionExecution.getBatchStatus());
        Assert.assertNull(partitionExecution.getExitStatus());
        Assert.assertNotNull(partitionExecution.getCreateTime());
        Assert.assertNotNull(partitionExecution.getUpdatedTime());
        Assert.assertNull(partitionExecution.getStartTime());
        Assert.assertNull(partitionExecution.getEndTime());
        Assert.assertEquals("persistent", partitionExecution.getPersistentUserData());
        Assert.assertEquals("reader", partitionExecution.getReaderCheckpoint());
        Assert.assertEquals("writer", partitionExecution.getWriterCheckpoint());

        _testEmptyMetrics(partitionExecution.getMetrics());

        final Properties params = partitionExecution.getPartitionParameters();
        Assert.assertNotNull(params);
        Assert.assertEquals("value", params.getProperty("test"));
        Assert.assertEquals(1, params.stringPropertyNames().size());
    }

    void _testEmptyMetrics(final Metric[] metrics) {
        Assert.assertNotNull(metrics);
        Assert.assertEquals(8, metrics.length);
        int seen = 0;
        for (final Metric metric : metrics) {
            Assert.assertNotNull(metric);
            switch (metric.getType()) {
                case READ_COUNT:
                    seen += 3;
                    Assert.assertEquals(0, metric.getValue());
                    break;
                case WRITE_COUNT:
                    seen += 5;
                    Assert.assertEquals(0, metric.getValue());
                    break;
                case COMMIT_COUNT:
                    seen += 7;
                    Assert.assertEquals(0, metric.getValue());
                    break;
                case ROLLBACK_COUNT:
                    seen += 11;
                    Assert.assertEquals(0, metric.getValue());
                    break;
                case READ_SKIP_COUNT:
                    seen += 13;
                    Assert.assertEquals(0, metric.getValue());
                    break;
                case PROCESS_SKIP_COUNT:
                    seen += 17;
                    Assert.assertEquals(0, metric.getValue());
                    break;
                case FILTER_COUNT:
                    seen += 19;
                    Assert.assertEquals(0, metric.getValue());
                    break;
                case WRITE_SKIP_COUNT:
                    seen += 23;
                    Assert.assertEquals(0, metric.getValue());
                    break;
                default:
                    Assert.fail(String.valueOf(metric.getType()));
            }
        }
        Assert.assertEquals(98, seen);
    }

    public void _testCopyMetrics(final Metric[] copy) {
        printMethodName();
        final Metric[] metrics = new Metric[] {
                new MetricImpl(Metric.MetricType.READ_COUNT, 1),
                new MetricImpl(Metric.MetricType.WRITE_COUNT, 2),
                new MetricImpl(Metric.MetricType.COMMIT_COUNT, 3),
                new MetricImpl(Metric.MetricType.ROLLBACK_COUNT, 4),
                new MetricImpl(Metric.MetricType.READ_SKIP_COUNT, 5),
                new MetricImpl(Metric.MetricType.PROCESS_SKIP_COUNT, 6),
                new MetricImpl(Metric.MetricType.FILTER_COUNT, 7),
                new MetricImpl(Metric.MetricType.WRITE_SKIP_COUNT, 8)
        };

        Assert.assertNotNull(copy);
        Assert.assertEquals(8, copy.length);

        int seen = 0;
        for (final Metric metric : copy) {
            switch (metric.getType()) {
                case READ_COUNT:
                    seen += 3;
                    Assert.assertEquals(1, metric.getValue());
                    break;
                case WRITE_COUNT:
                    seen += 5;
                    Assert.assertEquals(2, metric.getValue());
                    break;
                case COMMIT_COUNT:
                    seen += 7;
                    Assert.assertEquals(3, metric.getValue());
                    break;
                case ROLLBACK_COUNT:
                    seen += 11;
                    Assert.assertEquals(4, metric.getValue());
                    break;
                case READ_SKIP_COUNT:
                    seen += 13;
                    Assert.assertEquals(5, metric.getValue());
                    break;
                case PROCESS_SKIP_COUNT:
                    seen += 17;
                    Assert.assertEquals(6, metric.getValue());
                    break;
                case FILTER_COUNT:
                    seen += 19;
                    Assert.assertEquals(7, metric.getValue());
                    break;
                case WRITE_SKIP_COUNT:
                    seen += 23;
                    Assert.assertEquals(8, metric.getValue());
                    break;
                default:
                    Assert.fail(String.valueOf(metric.getType()));
            }
        }
        Assert.assertEquals(98, seen);
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void startJobExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job, "jsl");
        final ExtendedJobExecution old = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );

        repository().startJobExecution(
                old.getExecutionId(),
                new Date()
        );
        final ExtendedJobExecution jobExecution = repository().getJobExecution(
                old.getExecutionId()
        );

        Assert.assertEquals("job", jobExecution.getJobName());
        Assert.assertEquals(BatchStatus.STARTED, jobExecution.getBatchStatus());
        Assert.assertNull(jobExecution.getExitStatus());
        Assert.assertNotNull(jobExecution.getCreateTime());
        Assert.assertNotNull(jobExecution.getLastUpdatedTime());
        Assert.assertNotNull(jobExecution.getStartTime());
        Assert.assertNull(jobExecution.getEndTime());
        Assert.assertNull(jobExecution.getRestartElementId());
        final Properties params = jobExecution.getJobParameters();
        Assert.assertNotNull(params);
        Assert.assertEquals("value", params.getProperty("test"));
        Assert.assertEquals(1, params.stringPropertyNames().size());
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void updateJobExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job, "jsl");
        ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        //Test BatchStatuses
        for (final BatchStatus batchStatus : BatchStatus.values()) {
            repository().updateJobExecution(
                    jobExecution.getExecutionId(),
                    batchStatus,
                    new Date()
            );
            jobExecution = repository().getJobExecution(
                    jobExecution.getExecutionId()
            );

            Assert.assertEquals("job", jobExecution.getJobName());
            Assert.assertEquals(batchStatus, jobExecution.getBatchStatus());
            Assert.assertNull(jobExecution.getExitStatus());
            Assert.assertNotNull(jobExecution.getCreateTime());
            Assert.assertNotNull(jobExecution.getLastUpdatedTime());
            Assert.assertNull(jobExecution.getStartTime());
            Assert.assertNull(jobExecution.getEndTime());
            Assert.assertNull(jobExecution.getRestartElementId());
            final Properties params = jobExecution.getJobParameters();
            Assert.assertNotNull(params);
            Assert.assertEquals("value", params.getProperty("test"));
            Assert.assertEquals(1, params.stringPropertyNames().size());
        }
        repository().startJobExecution(
                jobExecution.getExecutionId(),
                new Date()
        );
        for (final BatchStatus batchStatus : BatchStatus.values()) {
            repository().updateJobExecution(
                    jobExecution.getExecutionId(),
                    batchStatus,
                    new Date()
            );
            jobExecution = repository().getJobExecution(
                    jobExecution.getExecutionId()
            );

            Assert.assertEquals("job", jobExecution.getJobName());
            Assert.assertEquals(batchStatus, jobExecution.getBatchStatus());
            Assert.assertNull(jobExecution.getExitStatus());
            Assert.assertNotNull(jobExecution.getCreateTime());
            Assert.assertNotNull(jobExecution.getLastUpdatedTime());
            Assert.assertNotNull(jobExecution.getStartTime());
            Assert.assertNull(jobExecution.getEndTime());
            Assert.assertNull(jobExecution.getRestartElementId());
            final Properties params = jobExecution.getJobParameters();
            Assert.assertNotNull(params);
            Assert.assertEquals("value", params.getProperty("test"));
            Assert.assertEquals(1, params.stringPropertyNames().size());
        }
        //Test throw
        try {
            repository().updateJobExecution(
                    Long.MIN_VALUE,
                    BatchStatus.COMPLETED,
                    new Date()
            );
            Assert.fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void finishJobExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job, "jsl");
        ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );

        repository().startJobExecution(
                jobExecution.getExecutionId(),
                new Date()
        );

        for (final BatchStatus batchStatus : BatchStatus.values()) {
            repository().finishJobExecution(
                    jobExecution.getExecutionId(),
                    batchStatus,
                    batchStatus.name(),
                    null,
                    new Date()
            );
            jobExecution = repository().getJobExecution(
                    jobExecution.getExecutionId()
            );

            Assert.assertEquals("job", jobExecution.getJobName());
            Assert.assertEquals(batchStatus, jobExecution.getBatchStatus());
            Assert.assertEquals(batchStatus.name(), jobExecution.getExitStatus());
            Assert.assertNotNull(jobExecution.getCreateTime());
            Assert.assertNotNull(jobExecution.getLastUpdatedTime());
            Assert.assertNotNull(jobExecution.getStartTime());
            Assert.assertNotNull(jobExecution.getEndTime());
            Assert.assertNull(jobExecution.getRestartElementId());
            final Properties params = jobExecution.getJobParameters();
            Assert.assertNotNull(params);
            Assert.assertEquals("value", params.getProperty("test"));
            Assert.assertEquals(1, params.stringPropertyNames().size());
        }
        repository().finishJobExecution(
                jobExecution.getExecutionId(),
                BatchStatus.COMPLETED,
                "Exit Status",
                "Exit Status",
                new Date()
        );
        jobExecution = repository().getJobExecution(
                jobExecution.getExecutionId()
        );

        Assert.assertEquals("job", jobExecution.getJobName());
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getBatchStatus());
        Assert.assertEquals("Exit Status", jobExecution.getExitStatus());
        Assert.assertNotNull(jobExecution.getCreateTime());
        Assert.assertNotNull(jobExecution.getLastUpdatedTime());
        Assert.assertNotNull(jobExecution.getStartTime());
        Assert.assertNotNull(jobExecution.getEndTime());
        Assert.assertEquals("Exit Status", jobExecution.getRestartElementId());
        final Properties params = jobExecution.getJobParameters();
        Assert.assertNotNull(params);
        Assert.assertEquals("value", params.getProperty("test"));
        Assert.assertEquals(1, params.stringPropertyNames().size());

        try {
            repository().finishJobExecution(
                    Long.MIN_VALUE,
                    BatchStatus.COMPLETED,
                    "Exit Status",
                    "Exit Status",
                    new Date()
            );
            Assert.fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void linkJobExecutionsTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void startStepExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job, "jsl");
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)),
                new Date()
        );

        Assert.assertEquals("step", stepExecution.getStepName());
        Assert.assertEquals(BatchStatus.STARTING, stepExecution.getBatchStatus());
        Assert.assertNull(stepExecution.getExitStatus());
        Assert.assertNotNull(stepExecution.getCreateTime());
        Assert.assertNotNull(stepExecution.getUpdatedTime());
        Assert.assertNull(stepExecution.getStartTime());
        Assert.assertNull(stepExecution.getEndTime());
        Assert.assertNull(stepExecution.getPersistentUserData());
        Assert.assertNull(stepExecution.getReaderCheckpoint());
        Assert.assertNull(stepExecution.getWriterCheckpoint());

        repository().startStepExecution(
                stepExecution.getStepExecutionId(),
                new Date()
        );
        stepExecution = repository().getStepExecution(stepExecution.getStepExecutionId());

        Assert.assertEquals("step", stepExecution.getStepName());
        Assert.assertEquals(BatchStatus.STARTED, stepExecution.getBatchStatus());
        Assert.assertNull(stepExecution.getExitStatus());
        Assert.assertNotNull(stepExecution.getCreateTime());
        Assert.assertNotNull(stepExecution.getUpdatedTime());
        Assert.assertNotNull(stepExecution.getStartTime());
        Assert.assertNull(stepExecution.getEndTime());
        Assert.assertNull(stepExecution.getPersistentUserData());
        Assert.assertNull(stepExecution.getReaderCheckpoint());
        Assert.assertNull(stepExecution.getWriterCheckpoint());

        _testEmptyMetrics(stepExecution.getMetrics());
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void updateStepExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void otherUpdateStepExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void finishStepExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void updatePartitionExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void otherUpdatePartitionExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void finishPartitionExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }

    //JobSecurityException,
    @Test
    public void getJobNames() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobException, JobSecurityException,
    @Test
    public void getJobInstanceCountTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobException, JobSecurityException,
    @Test
    public void getJobInstancesTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobException, JobSecurityException,
    @Test
    public void getRunningExecutionsTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getParametersTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getJobInstanceTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getJobInstanceForExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobInstanceException, JobSecurityException,
    @Test
    public void getJobExecutionsTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getJobExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }

    //JobRestartException, NoSuchJobExecutionException, NoSuchJobInstanceException, JobExecutionNotMostRecentException, JobSecurityException,
    @Test
    public void restartJobExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getStepExecutionsForJobTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getStepExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getPreviousStepExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getLatestStepExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getStepExecutionCountTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getStepExecutionsTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getUnfinishedPartitionExecutionsTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getPartitionExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }
}
