package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.core.element.JobImpl;
import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.jsl.fluent.Jsl;
import io.machinecode.chainlink.repository.core.MutableMetricImpl;
import io.machinecode.chainlink.spi.context.MutableMetric;
import io.machinecode.chainlink.spi.element.execution.Step;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class RepositoryTest extends BaseTest {

    private JobImpl _job() {
        return JobFactory.produce(Jsl.job("job")
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.batchlet("runBatchlet")
                                )
                ), PARAMETERS);
    }

    @Test
    public void createJobInstanceTest() throws Exception {
        printMethodName();

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        Assert.assertEquals("job", jobInstance.getJobName());
        Assert.assertEquals("jsl", jobInstance.getJslName());

        final ExtendedJobInstance second = repository().createJobInstance(job.getId(), "jsl", new Date());

        Assert.assertNotSame(jobInstance.getInstanceId(), second.getInstanceId());
    }

    @Test
    public void createJobExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
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
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        final ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)).getId(),
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

        _isEmptyMetrics(stepExecution.getMetrics());

        final ExtendedStepExecution second = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)).getId(),
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
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        final ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)).getId(),
                new Date()
        );

        final PartitionExecution partitionExecution = repository().createPartitionExecution(
                stepExecution.getStepExecutionId(),
                4,
                parameters,
                null,
                null,
                null,
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

        _isEmptyMetrics(partitionExecution.getMetrics());

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
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        final ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)).getId(),
                new Date()
        );

        PartitionExecution original = repository().createPartitionExecution(
                stepExecution.getStepExecutionId(),
                4,
                parameters,
                null,
                null,
                null,
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
                original.getPartitionId(),
                original.getPartitionParameters(),
                original.getPersistentUserData(),
                original.getReaderCheckpoint(),
                original.getWriterCheckpoint(),
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

        _isEmptyMetrics(partitionExecution.getMetrics());

        final Properties params = partitionExecution.getPartitionParameters();
        Assert.assertNotNull(params);
        Assert.assertEquals("value", params.getProperty("test"));
        Assert.assertEquals(1, params.stringPropertyNames().size());
    }

    @Test
    public void startJobExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
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

    @Test
    public void updateJobExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
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

    @Test
    public void finishJobExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
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

    //TODO This gets invalid results for getLatest if times are the same
    @Test
    public void linkJobExecutionsTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final Step<?,?> step = Step.class.cast(job.getExecutions().get(0));
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        ExtendedJobExecution first = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        Thread.sleep(1);
        ExtendedStepExecution firstS1 = repository().createStepExecution(
                first,
                step.getId(),
                new Date()
        );
        Thread.sleep(1);
        Assert.assertEquals(
                firstS1.getStepExecutionId(),
                repository().getLatestStepExecution(first.getExecutionId(), step.getId()).getStepExecutionId()
        );
        try {
            repository().getPreviousStepExecution(first.getExecutionId(), firstS1.getStepExecutionId(), step.getId());
            Assert.fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
        ExtendedStepExecution firstS2 = repository().createStepExecution(
                first,
                step.getId(),
                new Date()
        );
        Thread.sleep(1);
        Assert.assertEquals(
                firstS2.getStepExecutionId(),
                repository().getLatestStepExecution(first.getExecutionId(), step.getId()).getStepExecutionId()
        );
        Assert.assertEquals(
                firstS1.getStepExecutionId(),
                repository().getPreviousStepExecution(first.getExecutionId(), firstS2.getStepExecutionId(), step.getId()).getStepExecutionId()
        );

        ExtendedJobExecution second = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        Thread.sleep(1);
        ExtendedStepExecution secondS1 = repository().createStepExecution(
                second,
                step.getId(),
                new Date()
        );
        Thread.sleep(1);
        Assert.assertEquals(
                secondS1.getStepExecutionId(),
                repository().getLatestStepExecution(second.getExecutionId(), step.getId()).getStepExecutionId()
        );
        try {
            repository().getPreviousStepExecution(second.getExecutionId(), secondS1.getStepExecutionId(), step.getId());
            Assert.fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
        ExtendedStepExecution secondS2 = repository().createStepExecution(
                second,
                step.getId(),
                new Date()
        );
        Thread.sleep(1);
        Assert.assertEquals(
                secondS2.getStepExecutionId(),
                repository().getLatestStepExecution(second.getExecutionId(), step.getId()).getStepExecutionId()
        );
        Assert.assertEquals(
                secondS1.getStepExecutionId(),
                repository().getPreviousStepExecution(second.getExecutionId(), secondS2.getStepExecutionId(), step.getId()).getStepExecutionId()
        );

        ExtendedJobExecution third = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        Thread.sleep(1);
        ExtendedStepExecution thirdS1 = repository().createStepExecution(
                third,
                step.getId(),
                new Date()
        );
        Thread.sleep(1);
        Assert.assertEquals(
                thirdS1.getStepExecutionId(),
                repository().getLatestStepExecution(third.getExecutionId(), step.getId()).getStepExecutionId()
        );
        try {
            repository().getPreviousStepExecution(third.getExecutionId(), thirdS1.getStepExecutionId(), step.getId());
            Assert.fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
        ExtendedStepExecution thirdS2 = repository().createStepExecution(
                third,
                step.getId(),
                new Date()
        );
        Assert.assertEquals(
                thirdS2.getStepExecutionId(),
                repository().getLatestStepExecution(third.getExecutionId(), step.getId()).getStepExecutionId()
        );
        Assert.assertEquals(
                thirdS1.getStepExecutionId(),
                repository().getPreviousStepExecution(third.getExecutionId(), thirdS2.getStepExecutionId(), step.getId()).getStepExecutionId()
        );

        repository().linkJobExecutions(
                second.getExecutionId(),
                first.getExecutionId()
        );
        Assert.assertEquals(
                firstS2.getStepExecutionId(),
                repository().getPreviousStepExecution(second.getExecutionId(), secondS1.getStepExecutionId(), step.getId()).getStepExecutionId()
        );

        repository().linkJobExecutions(
                third.getExecutionId(),
                second.getExecutionId()
        );
        Assert.assertEquals(
                secondS2.getStepExecutionId(),
                repository().getPreviousStepExecution(third.getExecutionId(), thirdS1.getStepExecutionId(), step.getId()).getStepExecutionId()
        );
    }

    @Test
    public void startStepExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)).getId(),
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

        _isEmptyMetrics(stepExecution.getMetrics());
    }

    @Test
    public void updateStepExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)).getId(),
                new Date()
        );

        final MutableMetric[] metrics = new MutableMetric[] {
                new MutableMetricImpl(Metric.MetricType.READ_COUNT, 1),
                new MutableMetricImpl(Metric.MetricType.WRITE_COUNT, 2),
                new MutableMetricImpl(Metric.MetricType.COMMIT_COUNT, 3),
                new MutableMetricImpl(Metric.MetricType.ROLLBACK_COUNT, 4),
                new MutableMetricImpl(Metric.MetricType.READ_SKIP_COUNT, 5),
                new MutableMetricImpl(Metric.MetricType.PROCESS_SKIP_COUNT, 6),
                new MutableMetricImpl(Metric.MetricType.FILTER_COUNT, 7),
                new MutableMetricImpl(Metric.MetricType.WRITE_SKIP_COUNT, 8)
        };

        final Serializable persist = "persist";
        repository().updateStepExecution(
                stepExecution.getStepExecutionId(),
                metrics,
                persist,
                new Date()
        );
        stepExecution = repository().getStepExecution(
                stepExecution.getStepExecutionId()
        );
        final Serializable nextSerial = stepExecution.getPersistentUserData();

        Assert.assertEquals("step", stepExecution.getStepName());
        Assert.assertEquals(BatchStatus.STARTING, stepExecution.getBatchStatus());
        Assert.assertNull(stepExecution.getExitStatus());
        Assert.assertNotNull(stepExecution.getCreateTime());
        Assert.assertNotNull(stepExecution.getUpdatedTime());
        Assert.assertNull(stepExecution.getStartTime());
        Assert.assertNull(stepExecution.getEndTime());
        Assert.assertEquals(persist, nextSerial);
        Assert.assertFalse(persist == nextSerial);
        Assert.assertNull(stepExecution.getReaderCheckpoint());
        Assert.assertNull(stepExecution.getWriterCheckpoint());

        _isSameMetrics(metrics, stepExecution.getMetrics());

        for (final MutableMetric metric : metrics) {
            metric.increment();
        }

        repository().startStepExecution(
                stepExecution.getStepExecutionId(),
                new Date()
        );
        repository().updateStepExecution(
                stepExecution.getStepExecutionId(),
                metrics,
                nextSerial,
                new Date()
        );
        stepExecution = repository().getStepExecution(
                stepExecution.getStepExecutionId()
        );
        final Serializable thirdSerial = stepExecution.getPersistentUserData();

        Assert.assertEquals("step", stepExecution.getStepName());
        Assert.assertEquals(BatchStatus.STARTED, stepExecution.getBatchStatus());
        Assert.assertNull(stepExecution.getExitStatus());
        Assert.assertNotNull(stepExecution.getCreateTime());
        Assert.assertNotNull(stepExecution.getUpdatedTime());
        Assert.assertNotNull(stepExecution.getStartTime());
        Assert.assertNull(stepExecution.getEndTime());
        Assert.assertEquals(nextSerial, thirdSerial);
        Assert.assertFalse(nextSerial == thirdSerial);
        Assert.assertNull(stepExecution.getReaderCheckpoint());
        Assert.assertNull(stepExecution.getWriterCheckpoint());

        _isSameMetrics(metrics, stepExecution.getMetrics());

        try {
            repository().updateStepExecution(
                    Long.MIN_VALUE,
                    new Metric[0],
                    "",
                    new Date()
            );
            Assert.fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
    }

    @Test
    public void otherUpdateStepExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)).getId(),
                new Date()
        );

        final MutableMetric[] metrics = new MutableMetric[] {
                new MutableMetricImpl(Metric.MetricType.READ_COUNT, 1),
                new MutableMetricImpl(Metric.MetricType.WRITE_COUNT, 2),
                new MutableMetricImpl(Metric.MetricType.COMMIT_COUNT, 3),
                new MutableMetricImpl(Metric.MetricType.ROLLBACK_COUNT, 4),
                new MutableMetricImpl(Metric.MetricType.READ_SKIP_COUNT, 5),
                new MutableMetricImpl(Metric.MetricType.PROCESS_SKIP_COUNT, 6),
                new MutableMetricImpl(Metric.MetricType.FILTER_COUNT, 7),
                new MutableMetricImpl(Metric.MetricType.WRITE_SKIP_COUNT, 8)
        };

        final Serializable persist = "persist";
        final Serializable reader = "reader";
        final Serializable writer = "writer";
        repository().updateStepExecution(
                stepExecution.getStepExecutionId(),
                metrics,
                persist,
                reader,
                writer,
                new Date()
        );
        stepExecution = repository().getStepExecution(
                stepExecution.getStepExecutionId()
        );
        final Serializable nextPersist = stepExecution.getPersistentUserData();
        final Serializable nextReader = stepExecution.getReaderCheckpoint();
        final Serializable nextWriter = stepExecution.getWriterCheckpoint();

        Assert.assertEquals("step", stepExecution.getStepName());
        Assert.assertEquals(BatchStatus.STARTING, stepExecution.getBatchStatus());
        Assert.assertNull(stepExecution.getExitStatus());
        Assert.assertNotNull(stepExecution.getCreateTime());
        Assert.assertNotNull(stepExecution.getUpdatedTime());
        Assert.assertNull(stepExecution.getStartTime());
        Assert.assertNull(stepExecution.getEndTime());
        Assert.assertNotNull(nextPersist);
        Assert.assertNotNull(nextReader);
        Assert.assertNotNull(nextWriter);
        Assert.assertEquals(persist, nextPersist);
        Assert.assertFalse(persist == nextPersist);
        Assert.assertEquals(reader, nextReader);
        Assert.assertFalse(reader == nextReader);
        Assert.assertEquals(writer, nextWriter);
        Assert.assertFalse(writer == nextWriter);

        _isSameMetrics(metrics, stepExecution.getMetrics());

        for (final MutableMetric metric : metrics) {
            metric.increment();
        }

        repository().startStepExecution(
                stepExecution.getStepExecutionId(),
                new Date()
        );
        repository().updateStepExecution(
                stepExecution.getStepExecutionId(),
                metrics,
                persist,
                reader,
                writer,
                new Date()
        );
        stepExecution = repository().getStepExecution(
                stepExecution.getStepExecutionId()
        );
        final Serializable thirdPersist = stepExecution.getPersistentUserData();
        final Serializable thirdReader = stepExecution.getReaderCheckpoint();
        final Serializable thirdWriter = stepExecution.getWriterCheckpoint();

        Assert.assertEquals("step", stepExecution.getStepName());
        Assert.assertEquals(BatchStatus.STARTED, stepExecution.getBatchStatus());
        Assert.assertNull(stepExecution.getExitStatus());
        Assert.assertNotNull(stepExecution.getCreateTime());
        Assert.assertNotNull(stepExecution.getUpdatedTime());
        Assert.assertNotNull(stepExecution.getStartTime());
        Assert.assertNull(stepExecution.getEndTime());
        Assert.assertNotNull(thirdPersist);
        Assert.assertNotNull(thirdReader);
        Assert.assertNotNull(thirdWriter);
        Assert.assertEquals(nextPersist, thirdPersist);
        Assert.assertFalse(nextPersist == thirdPersist);
        Assert.assertEquals(nextReader, thirdReader);
        Assert.assertFalse(nextReader == thirdReader);
        Assert.assertEquals(nextWriter, thirdWriter);
        Assert.assertFalse(nextWriter == thirdWriter);

        _isSameMetrics(metrics, stepExecution.getMetrics());

        try {
            repository().updateStepExecution(
                    Long.MIN_VALUE,
                    new Metric[0],
                    "",
                    new Date()
            );
            Assert.fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
    }

    @Test
    public void finishStepExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)).getId(),
                new Date()
        );
        repository().startStepExecution(
                stepExecution.getStepExecutionId(),
                new Date()
        );

        final MutableMetric[] metrics = new MutableMetric[] {
                new MutableMetricImpl(Metric.MetricType.READ_COUNT, 1),
                new MutableMetricImpl(Metric.MetricType.WRITE_COUNT, 2),
                new MutableMetricImpl(Metric.MetricType.COMMIT_COUNT, 3),
                new MutableMetricImpl(Metric.MetricType.ROLLBACK_COUNT, 4),
                new MutableMetricImpl(Metric.MetricType.READ_SKIP_COUNT, 5),
                new MutableMetricImpl(Metric.MetricType.PROCESS_SKIP_COUNT, 6),
                new MutableMetricImpl(Metric.MetricType.FILTER_COUNT, 7),
                new MutableMetricImpl(Metric.MetricType.WRITE_SKIP_COUNT, 8)
        };

        repository().finishStepExecution(
                stepExecution.getStepExecutionId(),
                metrics,
                BatchStatus.FAILED,
                "some",
                new Date()
        );
        stepExecution = repository().getStepExecution(
                stepExecution.getStepExecutionId()
        );

        Assert.assertEquals("step", stepExecution.getStepName());
        Assert.assertEquals(BatchStatus.FAILED, stepExecution.getBatchStatus());
        Assert.assertEquals("some", stepExecution.getExitStatus());
        Assert.assertNotNull(stepExecution.getCreateTime());
        Assert.assertNotNull(stepExecution.getUpdatedTime());
        Assert.assertNotNull(stepExecution.getStartTime());
        Assert.assertNotNull(stepExecution.getEndTime());
        Assert.assertNull(stepExecution.getPersistentUserData());
        Assert.assertNull(stepExecution.getReaderCheckpoint());
        Assert.assertNull(stepExecution.getWriterCheckpoint());

        _isSameMetrics(metrics, stepExecution.getMetrics());

        try {
            repository().finishStepExecution(
                    Long.MIN_VALUE,
                    new Metric[0],
                    BatchStatus.FAILED,
                    "",
                    new Date()
            );
            Assert.fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
    }

    @Test
    public void startPartitionExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)).getId(),
                new Date()
        );
        PartitionExecution partition = repository().createPartitionExecution(
                stepExecution.getStepExecutionId(),
                4,
                parameters,
                null,
                null,
                null,
                new Date()
        );
        repository().startPartitionExecution(
                partition.getPartitionExecutionId(),
                new Date()
        );
        partition = repository().getPartitionExecution(
                partition.getPartitionExecutionId()
        );

        Assert.assertEquals(stepExecution.getStepExecutionId(), partition.getStepExecutionId());
        Assert.assertEquals(4, partition.getPartitionId());
        Assert.assertEquals(BatchStatus.STARTED, partition.getBatchStatus());
        Assert.assertNull(partition.getExitStatus());
        Assert.assertNotNull(partition.getCreateTime());
        Assert.assertNotNull(partition.getUpdatedTime());
        Assert.assertNotNull(partition.getStartTime());
        Assert.assertNull(partition.getEndTime());
        Assert.assertNull(partition.getPersistentUserData());
        Assert.assertNull(partition.getReaderCheckpoint());
        Assert.assertNull(partition.getWriterCheckpoint());

        _isEmptyMetrics(partition.getMetrics());

        final Properties params = partition.getPartitionParameters();
        Assert.assertNotNull(params);
        Assert.assertEquals("value", params.getProperty("test"));
        Assert.assertEquals(1, params.stringPropertyNames().size());


        PartitionExecution otherPartition = repository().createPartitionExecution(
                stepExecution.getStepExecutionId(),
                partition.getPartitionId(),
                partition.getPartitionParameters(),
                partition.getPersistentUserData(),
                partition.getReaderCheckpoint(),
                partition.getWriterCheckpoint(),
                new Date()
        );
        repository().startPartitionExecution(
                otherPartition.getPartitionExecutionId(),
                new Date()
        );
        otherPartition = repository().getPartitionExecution(
                otherPartition.getPartitionExecutionId()
        );

        Assert.assertEquals(stepExecution.getStepExecutionId(), otherPartition.getStepExecutionId());
        Assert.assertEquals(4, otherPartition.getPartitionId());
        Assert.assertEquals(BatchStatus.STARTED, otherPartition.getBatchStatus());
        Assert.assertNull(otherPartition.getExitStatus());
        Assert.assertNotNull(otherPartition.getCreateTime());
        Assert.assertNotNull(otherPartition.getUpdatedTime());
        Assert.assertNotNull(otherPartition.getStartTime());
        Assert.assertNull(otherPartition.getEndTime());
        Assert.assertNull(otherPartition.getPersistentUserData());
        Assert.assertNull(otherPartition.getReaderCheckpoint());
        Assert.assertNull(otherPartition.getWriterCheckpoint());

        _isEmptyMetrics(otherPartition.getMetrics());

        final Properties otherParams = otherPartition.getPartitionParameters();
        Assert.assertNotNull(otherParams);
        Assert.assertEquals("value", otherParams.getProperty("test"));
        Assert.assertEquals(1, otherParams.stringPropertyNames().size());

        try {
            repository().startPartitionExecution(
                    Long.MIN_VALUE,
                    new Date()
            );
            Assert.fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
    }

    @Test
    public void updatePartitionExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)).getId(),
                new Date()
        );
        PartitionExecution partition = repository().createPartitionExecution(
                stepExecution.getStepExecutionId(),
                4,
                parameters,
                null,
                null,
                null,
                new Date()
        );

        final MutableMetric[] metrics = new MutableMetric[] {
                new MutableMetricImpl(Metric.MetricType.READ_COUNT, 1),
                new MutableMetricImpl(Metric.MetricType.WRITE_COUNT, 2),
                new MutableMetricImpl(Metric.MetricType.COMMIT_COUNT, 3),
                new MutableMetricImpl(Metric.MetricType.ROLLBACK_COUNT, 4),
                new MutableMetricImpl(Metric.MetricType.READ_SKIP_COUNT, 5),
                new MutableMetricImpl(Metric.MetricType.PROCESS_SKIP_COUNT, 6),
                new MutableMetricImpl(Metric.MetricType.FILTER_COUNT, 7),
                new MutableMetricImpl(Metric.MetricType.WRITE_SKIP_COUNT, 8)
        };

        final Serializable persist = "persist";
        final Serializable reader = "reader";
        final Serializable writer = "writer";
        repository().updatePartitionExecution(
                partition.getPartitionExecutionId(),
                metrics,
                persist,
                reader,
                writer,
                new Date()
        );
        partition = repository().getPartitionExecution(
                partition.getPartitionExecutionId()
        );
        final Serializable nextPersist = partition.getPersistentUserData();
        final Serializable nextReader = partition.getReaderCheckpoint();
        final Serializable nextWriter = partition.getWriterCheckpoint();

        Assert.assertEquals(stepExecution.getStepExecutionId(), partition.getStepExecutionId());
        Assert.assertEquals(4, partition.getPartitionId());
        Assert.assertEquals(BatchStatus.STARTING, partition.getBatchStatus());
        Assert.assertNull(partition.getExitStatus());
        Assert.assertNotNull(partition.getCreateTime());
        Assert.assertNotNull(partition.getUpdatedTime());
        Assert.assertNull(partition.getStartTime());
        Assert.assertNull(partition.getEndTime());
        Assert.assertNotNull(nextPersist);
        Assert.assertNotNull(nextReader);
        Assert.assertNotNull(nextWriter);
        Assert.assertEquals(persist, nextPersist);
        Assert.assertFalse(persist == nextPersist);
        Assert.assertEquals(reader, nextReader);
        Assert.assertFalse(reader == nextReader);
        Assert.assertEquals(writer, nextWriter);
        Assert.assertFalse(writer == nextWriter);

        _isSameMetrics(metrics, partition.getMetrics());

        final Properties params = partition.getPartitionParameters();
        Assert.assertNotNull(params);
        Assert.assertEquals("value", params.getProperty("test"));
        Assert.assertEquals(1, params.stringPropertyNames().size());

        for (final MutableMetric metric : metrics) {
            metric.increment();
        }

        repository().startPartitionExecution(
                partition.getPartitionExecutionId(),
                new Date()
        );
        repository().updatePartitionExecution(
                partition.getPartitionExecutionId(),
                metrics,
                nextPersist,
                nextReader,
                nextWriter,
                new Date()
        );
        partition = repository().getPartitionExecution(
                partition.getPartitionExecutionId()
        );

        final Serializable thirdPersist = partition.getPersistentUserData();
        final Serializable thirdReader = partition.getReaderCheckpoint();
        final Serializable thirdWriter = partition.getWriterCheckpoint();

        Assert.assertEquals(BatchStatus.STARTED, partition.getBatchStatus());
        Assert.assertNull(partition.getExitStatus());
        Assert.assertNotNull(partition.getCreateTime());
        Assert.assertNotNull(partition.getUpdatedTime());
        Assert.assertNotNull(partition.getStartTime());
        Assert.assertNull(partition.getEndTime());
        Assert.assertNotNull(thirdPersist);
        Assert.assertNotNull(thirdReader);
        Assert.assertNotNull(thirdWriter);
        Assert.assertEquals(nextPersist, thirdPersist);
        Assert.assertFalse(nextPersist == thirdPersist);
        Assert.assertEquals(nextReader, thirdReader);
        Assert.assertFalse(nextReader == thirdReader);
        Assert.assertEquals(nextWriter, thirdWriter);
        Assert.assertFalse(nextWriter == thirdWriter);

        _isSameMetrics(metrics, partition.getMetrics());





        PartitionExecution otherPartition = repository().createPartitionExecution(
                stepExecution.getStepExecutionId(),
                partition.getPartitionId(),
                partition.getPartitionParameters(),
                partition.getPersistentUserData(),
                partition.getReaderCheckpoint(),
                partition.getWriterCheckpoint(),
                new Date()
        );
        repository().updatePartitionExecution(
                otherPartition.getPartitionExecutionId(),
                metrics,
                persist,
                reader,
                writer,
                new Date()
        );
        otherPartition = repository().getPartitionExecution(
                otherPartition.getPartitionExecutionId()
        );
        final Serializable otherNextPersist = otherPartition.getPersistentUserData();
        final Serializable otherNextReader = otherPartition.getReaderCheckpoint();
        final Serializable otherNextWriter = otherPartition.getWriterCheckpoint();

        Assert.assertEquals(stepExecution.getStepExecutionId(), otherPartition.getStepExecutionId());
        Assert.assertEquals(4, otherPartition.getPartitionId());
        Assert.assertEquals(BatchStatus.STARTING, otherPartition.getBatchStatus());
        Assert.assertNull(otherPartition.getExitStatus());
        Assert.assertNotNull(otherPartition.getCreateTime());
        Assert.assertNotNull(otherPartition.getUpdatedTime());
        Assert.assertNull(otherPartition.getStartTime());
        Assert.assertNull(otherPartition.getEndTime());
        Assert.assertNotNull(otherNextPersist);
        Assert.assertNotNull(otherNextReader);
        Assert.assertNotNull(otherNextWriter);
        Assert.assertEquals(persist, otherNextPersist);
        Assert.assertFalse(persist == otherNextPersist);
        Assert.assertEquals(reader, otherNextReader);
        Assert.assertFalse(reader == otherNextReader);
        Assert.assertEquals(writer, otherNextWriter);
        Assert.assertFalse(writer == otherNextWriter);

        _isSameMetrics(metrics, otherPartition.getMetrics());

        final Properties otherParams = otherPartition.getPartitionParameters();
        Assert.assertNotNull(otherParams);
        Assert.assertEquals("value", otherParams.getProperty("test"));
        Assert.assertEquals(1, otherParams.stringPropertyNames().size());

        for (final MutableMetric metric : metrics) {
            metric.increment();
        }

        repository().startPartitionExecution(
                otherPartition.getPartitionExecutionId(),
                new Date()
        );
        repository().updatePartitionExecution(
                otherPartition.getPartitionExecutionId(),
                metrics,
                nextPersist,
                nextReader,
                nextWriter,
                new Date()
        );
        otherPartition = repository().getPartitionExecution(
                otherPartition.getPartitionExecutionId()
        );

        final Serializable otherThirdPersist = otherPartition.getPersistentUserData();
        final Serializable otherThirdReader = otherPartition.getReaderCheckpoint();
        final Serializable otherThirdWriter = otherPartition.getWriterCheckpoint();

        Assert.assertEquals(BatchStatus.STARTED, otherPartition.getBatchStatus());
        Assert.assertNull(otherPartition.getExitStatus());
        Assert.assertNotNull(otherPartition.getCreateTime());
        Assert.assertNotNull(otherPartition.getUpdatedTime());
        Assert.assertNotNull(otherPartition.getStartTime());
        Assert.assertNull(otherPartition.getEndTime());
        Assert.assertNotNull(otherThirdPersist);
        Assert.assertNotNull(otherThirdReader);
        Assert.assertNotNull(otherThirdWriter);
        Assert.assertEquals(otherNextPersist, otherThirdPersist);
        Assert.assertFalse(otherNextPersist == otherThirdPersist);
        Assert.assertEquals(otherNextReader, otherThirdReader);
        Assert.assertFalse(otherNextReader == otherThirdReader);
        Assert.assertEquals(otherNextWriter, otherThirdWriter);
        Assert.assertFalse(otherNextWriter == otherThirdWriter);

        _isSameMetrics(metrics, otherPartition.getMetrics());

        try {
            repository().updatePartitionExecution(
                    Long.MIN_VALUE,
                    metrics,
                    persist,
                    reader,
                    writer,
                    new Date()
            );
            Assert.fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
    }

    @Test
    public void finishPartitionExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)).getId(),
                new Date()
        );
        PartitionExecution partition = repository().createPartitionExecution(
                stepExecution.getStepExecutionId(),
                4,
                parameters,
                null,
                null,
                null,
                new Date()
        );

        final MutableMetric[] metrics = new MutableMetric[] {
                new MutableMetricImpl(Metric.MetricType.READ_COUNT, 1),
                new MutableMetricImpl(Metric.MetricType.WRITE_COUNT, 2),
                new MutableMetricImpl(Metric.MetricType.COMMIT_COUNT, 3),
                new MutableMetricImpl(Metric.MetricType.ROLLBACK_COUNT, 4),
                new MutableMetricImpl(Metric.MetricType.READ_SKIP_COUNT, 5),
                new MutableMetricImpl(Metric.MetricType.PROCESS_SKIP_COUNT, 6),
                new MutableMetricImpl(Metric.MetricType.FILTER_COUNT, 7),
                new MutableMetricImpl(Metric.MetricType.WRITE_SKIP_COUNT, 8)
        };

        final String exitStatus = "exit status";
        final String otherExitStatus = "other exit status";
        final Serializable persist = "persist";
        repository().finishPartitionExecution(
                partition.getPartitionExecutionId(),
                metrics,
                persist,
                BatchStatus.FAILED,
                exitStatus,
                new Date()
        );
        partition = repository().getPartitionExecution(
                partition.getPartitionExecutionId()
        );
        final Serializable nextPersist = partition.getPersistentUserData();

        Assert.assertEquals(stepExecution.getStepExecutionId(), partition.getStepExecutionId());
        Assert.assertEquals(4, partition.getPartitionId());
        Assert.assertEquals(BatchStatus.FAILED, partition.getBatchStatus());
        Assert.assertEquals(exitStatus, partition.getExitStatus());
        Assert.assertNotNull(partition.getCreateTime());
        Assert.assertNotNull(partition.getUpdatedTime());
        Assert.assertNull(partition.getStartTime());
        Assert.assertNotNull(partition.getEndTime());
        Assert.assertNotNull(nextPersist);
        Assert.assertNull(partition.getReaderCheckpoint());
        Assert.assertNull(partition.getWriterCheckpoint());
        Assert.assertEquals(persist, nextPersist);
        Assert.assertFalse(persist == nextPersist);

        _isSameMetrics(metrics, partition.getMetrics());

        final Properties params = partition.getPartitionParameters();
        Assert.assertNotNull(params);
        Assert.assertEquals("value", params.getProperty("test"));
        Assert.assertEquals(1, params.stringPropertyNames().size());

        for (final MutableMetric metric : metrics) {
            metric.increment();
        }


        partition = repository().createPartitionExecution(
                stepExecution.getStepExecutionId(),
                5,
                parameters,
                null,
                null,
                null,
                new Date()
        );
        repository().startPartitionExecution(
                partition.getPartitionExecutionId(),
                new Date()
        );
        repository().finishPartitionExecution(
                partition.getPartitionExecutionId(),
                metrics,
                persist,
                BatchStatus.COMPLETED,
                exitStatus,
                new Date()
        );
        partition = repository().getPartitionExecution(
                partition.getPartitionExecutionId()
        );

        final Serializable thirdPersist = partition.getPersistentUserData();

        Assert.assertEquals(stepExecution.getStepExecutionId(), partition.getStepExecutionId());
        Assert.assertEquals(5, partition.getPartitionId());
        Assert.assertEquals(BatchStatus.COMPLETED, partition.getBatchStatus());
        Assert.assertEquals(exitStatus, partition.getExitStatus());
        Assert.assertNotNull(partition.getCreateTime());
        Assert.assertNotNull(partition.getUpdatedTime());
        Assert.assertNotNull(partition.getStartTime());
        Assert.assertNotNull(partition.getEndTime());
        Assert.assertNotNull(thirdPersist);
        Assert.assertNull(partition.getReaderCheckpoint());
        Assert.assertNull(partition.getWriterCheckpoint());
        Assert.assertEquals(nextPersist, thirdPersist);
        Assert.assertFalse(nextPersist == thirdPersist);

        _isSameMetrics(metrics, partition.getMetrics());





        PartitionExecution otherPartition = repository().createPartitionExecution(
                stepExecution.getStepExecutionId(),
                partition.getPartitionId(),
                partition.getPartitionParameters(),
                partition.getPersistentUserData(),
                partition.getReaderCheckpoint(),
                partition.getWriterCheckpoint(),
                new Date()
        );
        repository().finishPartitionExecution(
                otherPartition.getPartitionExecutionId(),
                metrics,
                persist,
                BatchStatus.STOPPED,
                otherExitStatus,
                new Date()
        );
        otherPartition = repository().getPartitionExecution(
                otherPartition.getPartitionExecutionId()
        );
        final Serializable otherNextPersist = otherPartition.getPersistentUserData();

        Assert.assertEquals(stepExecution.getStepExecutionId(), otherPartition.getStepExecutionId());
        Assert.assertEquals(5, otherPartition.getPartitionId());
        Assert.assertEquals(BatchStatus.STOPPED, otherPartition.getBatchStatus());
        Assert.assertEquals(otherExitStatus, otherPartition.getExitStatus());
        Assert.assertNotNull(otherPartition.getCreateTime());
        Assert.assertNotNull(otherPartition.getUpdatedTime());
        Assert.assertNull(otherPartition.getStartTime());
        Assert.assertNotNull(otherPartition.getEndTime());
        Assert.assertNotNull(otherNextPersist);
        Assert.assertNull(otherPartition.getReaderCheckpoint());
        Assert.assertNull(otherPartition.getWriterCheckpoint());
        Assert.assertEquals(persist, otherNextPersist);
        Assert.assertFalse(persist == otherNextPersist);

        _isSameMetrics(metrics, otherPartition.getMetrics());

        final Properties otherParams = otherPartition.getPartitionParameters();
        Assert.assertNotNull(otherParams);
        Assert.assertEquals("value", otherParams.getProperty("test"));
        Assert.assertEquals(1, otherParams.stringPropertyNames().size());

        for (final MutableMetric metric : metrics) {
            metric.increment();
        }


        otherPartition = repository().createPartitionExecution(
                stepExecution.getStepExecutionId(),
                partition.getPartitionId(),
                partition.getPartitionParameters(),
                partition.getPersistentUserData(),
                partition.getReaderCheckpoint(),
                partition.getWriterCheckpoint(),
                new Date()
        );
        repository().startPartitionExecution(
                otherPartition.getPartitionExecutionId(),
                new Date()
        );
        repository().finishPartitionExecution(
                otherPartition.getPartitionExecutionId(),
                metrics,
                nextPersist,
                BatchStatus.COMPLETED,
                exitStatus,
                new Date()
        );
        otherPartition = repository().getPartitionExecution(
                otherPartition.getPartitionExecutionId()
        );

        final Serializable otherThirdPersist = otherPartition.getPersistentUserData();

        Assert.assertEquals(stepExecution.getStepExecutionId(), otherPartition.getStepExecutionId());
        Assert.assertEquals(5, otherPartition.getPartitionId());
        Assert.assertEquals(BatchStatus.COMPLETED, otherPartition.getBatchStatus());
        Assert.assertEquals(exitStatus, otherPartition.getExitStatus());
        Assert.assertNotNull(otherPartition.getCreateTime());
        Assert.assertNotNull(otherPartition.getUpdatedTime());
        Assert.assertNotNull(otherPartition.getStartTime());
        Assert.assertNotNull(otherPartition.getEndTime());
        Assert.assertNotNull(otherThirdPersist);
        Assert.assertNull(otherPartition.getReaderCheckpoint());
        Assert.assertNull(otherPartition.getWriterCheckpoint());
        Assert.assertEquals(otherNextPersist, otherThirdPersist);
        Assert.assertFalse(otherNextPersist == otherThirdPersist);

        _isSameMetrics(metrics, otherPartition.getMetrics());


        try {
            repository().finishPartitionExecution(
                    Long.MIN_VALUE,
                    metrics,
                    persist,
                    BatchStatus.FAILED,
                    "",
                    new Date()
            );
            Assert.fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
    }

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

    private void _isEmptyMetrics(final Metric[] metrics) {
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

    /*
    private void _isCopyMetrics(final Metric[] copy) {
        printMethodName();

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
    */

    private void _isSameMetrics(final Metric[] source, final Metric[] target) {
        Assert.assertNotNull(source);
        Assert.assertEquals(8, source.length);

        int seen = 0;
        outer: for (final Metric metric : source) {
            switch (metric.getType()) {
                case READ_COUNT:
                    seen += 3;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.READ_COUNT) {
                            Assert.assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    Assert.fail();
                case WRITE_COUNT:
                    seen += 5;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.WRITE_COUNT) {
                            Assert.assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    Assert.fail();
                case COMMIT_COUNT:
                    seen += 7;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.COMMIT_COUNT) {
                            Assert.assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    Assert.fail();
                case ROLLBACK_COUNT:
                    seen += 11;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.ROLLBACK_COUNT) {
                            Assert.assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    Assert.fail();
                case READ_SKIP_COUNT:
                    seen += 13;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.READ_SKIP_COUNT) {
                            Assert.assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    Assert.fail();
                case PROCESS_SKIP_COUNT:
                    seen += 17;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.PROCESS_SKIP_COUNT) {
                            Assert.assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    Assert.fail();
                case FILTER_COUNT:
                    seen += 19;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.FILTER_COUNT) {
                            Assert.assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    Assert.fail();
                case WRITE_SKIP_COUNT:
                    seen += 23;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.WRITE_SKIP_COUNT) {
                            Assert.assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    Assert.fail();
                default:
                    Assert.fail(String.valueOf(metric.getType()));
            }
        }
        Assert.assertEquals(98, seen);
    }
}
