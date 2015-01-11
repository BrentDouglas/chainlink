package io.machinecode.chainlink.core.repository;

import io.machinecode.chainlink.core.element.JobImpl;
import io.machinecode.chainlink.core.base.BaseTest;
import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.context.MutableMetric;
import io.machinecode.chainlink.spi.element.execution.Step;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
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
public class RepositoryTest extends BaseTest {

    private JobImpl _job() {
        return JobFactory.produce(Jsl.job("job")
                .addExecution(
                        Jsl.step("step1")
                                .setTask(
                                        Jsl.batchlet("runBatchlet")
                                )
                ).addExecution(
                        Jsl.step("step2")
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
        assertEquals("job", jobInstance.getJobName());
        assertEquals("jsl", jobInstance.getJslName());

        final ExtendedJobInstance second = repository().createJobInstance(job.getId(), "jsl", new Date());

        assertNotSame(jobInstance.getInstanceId(), second.getInstanceId());
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

        assertEquals("job", jobExecution.getJobName());
        assertEquals(BatchStatus.STARTING, jobExecution.getBatchStatus());
        assertNull(jobExecution.getExitStatus());
        assertNotNull(jobExecution.getCreateTime());
        assertNotNull(jobExecution.getLastUpdatedTime());
        assertNull(jobExecution.getStartTime());
        assertNull(jobExecution.getEndTime());
        assertNull(jobExecution.getRestartElementId());
        final Properties params = jobExecution.getJobParameters();
        assertNotNull(params);
        assertEquals("value", params.getProperty("test"));
        assertEquals(1, params.stringPropertyNames().size());

        final ExtendedJobExecution second = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );

        assertNotSame(jobExecution.getExecutionId(), second.getExecutionId());
    }

    @Test
    public void createStepExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final Step<?,?> step1 = Step.class.cast(job.getExecutions().get(0));
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        final ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                step1.getId(),
                new Date()
        );

        assertEquals(step1.getId(), stepExecution.getStepName());
        assertEquals(BatchStatus.STARTING, stepExecution.getBatchStatus());
        assertNull(stepExecution.getExitStatus());
        assertNotNull(stepExecution.getCreateTime());
        assertNotNull(stepExecution.getUpdatedTime());
        assertNull(stepExecution.getStartTime());
        assertNull(stepExecution.getEndTime());
        assertNull(stepExecution.getPersistentUserData());
        assertNull(stepExecution.getReaderCheckpoint());
        assertNull(stepExecution.getWriterCheckpoint());

        _isEmptyMetrics(stepExecution.getMetrics());

        final ExtendedStepExecution second = repository().createStepExecution(
                jobExecution,
                Step.class.cast(job.getExecutions().get(0)).getId(),
                new Date()
        );

        assertNotSame(stepExecution.getStepExecutionId(), second.getStepExecutionId());
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

        assertEquals(stepExecution.getStepExecutionId(), partitionExecution.getStepExecutionId());
        assertEquals(4, partitionExecution.getPartitionId());
        assertEquals(BatchStatus.STARTING, partitionExecution.getBatchStatus());
        assertNull(partitionExecution.getExitStatus());
        assertNotNull(partitionExecution.getCreateTime());
        assertNotNull(partitionExecution.getUpdatedTime());
        assertNull(partitionExecution.getStartTime());
        assertNull(partitionExecution.getEndTime());
        assertNull(partitionExecution.getPersistentUserData());
        assertNull(partitionExecution.getReaderCheckpoint());
        assertNull(partitionExecution.getWriterCheckpoint());

        _isEmptyMetrics(partitionExecution.getMetrics());

        final Properties params = partitionExecution.getPartitionParameters();
        assertNotNull(params);
        assertEquals("value", params.getProperty("test"));
        assertEquals(1, params.stringPropertyNames().size());
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

        assertEquals(stepExecution.getStepExecutionId(), partitionExecution.getStepExecutionId());
        assertEquals(4, partitionExecution.getPartitionId());
        assertEquals(BatchStatus.STARTING, partitionExecution.getBatchStatus());
        assertNull(partitionExecution.getExitStatus());
        assertNotNull(partitionExecution.getCreateTime());
        assertNotNull(partitionExecution.getUpdatedTime());
        assertNull(partitionExecution.getStartTime());
        assertNull(partitionExecution.getEndTime());
        assertEquals("persistent", partitionExecution.getPersistentUserData());
        assertEquals("reader", partitionExecution.getReaderCheckpoint());
        assertEquals("writer", partitionExecution.getWriterCheckpoint());

        _isEmptyMetrics(partitionExecution.getMetrics());

        final Properties params = partitionExecution.getPartitionParameters();
        assertNotNull(params);
        assertEquals("value", params.getProperty("test"));
        assertEquals(1, params.stringPropertyNames().size());
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

        assertEquals("job", jobExecution.getJobName());
        assertEquals(BatchStatus.STARTED, jobExecution.getBatchStatus());
        assertNull(jobExecution.getExitStatus());
        assertNotNull(jobExecution.getCreateTime());
        assertNotNull(jobExecution.getLastUpdatedTime());
        assertNotNull(jobExecution.getStartTime());
        assertNull(jobExecution.getEndTime());
        assertNull(jobExecution.getRestartElementId());
        final Properties params = jobExecution.getJobParameters();
        assertNotNull(params);
        assertEquals("value", params.getProperty("test"));
        assertEquals(1, params.stringPropertyNames().size());
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

            assertEquals("job", jobExecution.getJobName());
            assertEquals(batchStatus, jobExecution.getBatchStatus());
            assertNull(jobExecution.getExitStatus());
            assertNotNull(jobExecution.getCreateTime());
            assertNotNull(jobExecution.getLastUpdatedTime());
            assertNull(jobExecution.getStartTime());
            assertNull(jobExecution.getEndTime());
            assertNull(jobExecution.getRestartElementId());
            final Properties params = jobExecution.getJobParameters();
            assertNotNull(params);
            assertEquals("value", params.getProperty("test"));
            assertEquals(1, params.stringPropertyNames().size());
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

            assertEquals("job", jobExecution.getJobName());
            assertEquals(batchStatus, jobExecution.getBatchStatus());
            assertNull(jobExecution.getExitStatus());
            assertNotNull(jobExecution.getCreateTime());
            assertNotNull(jobExecution.getLastUpdatedTime());
            assertNotNull(jobExecution.getStartTime());
            assertNull(jobExecution.getEndTime());
            assertNull(jobExecution.getRestartElementId());
            final Properties params = jobExecution.getJobParameters();
            assertNotNull(params);
            assertEquals("value", params.getProperty("test"));
            assertEquals(1, params.stringPropertyNames().size());
        }
        try {
            repository().updateJobExecution(
                    Long.MIN_VALUE,
                    BatchStatus.COMPLETED,
                    new Date()
            );
            fail();
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

            assertEquals("job", jobExecution.getJobName());
            assertEquals(batchStatus, jobExecution.getBatchStatus());
            assertEquals(batchStatus.name(), jobExecution.getExitStatus());
            assertNotNull(jobExecution.getCreateTime());
            assertNotNull(jobExecution.getLastUpdatedTime());
            assertNotNull(jobExecution.getStartTime());
            assertNotNull(jobExecution.getEndTime());
            assertNull(jobExecution.getRestartElementId());
            final Properties params = jobExecution.getJobParameters();
            assertNotNull(params);
            assertEquals("value", params.getProperty("test"));
            assertEquals(1, params.stringPropertyNames().size());
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

        assertEquals("job", jobExecution.getJobName());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getBatchStatus());
        assertEquals("Exit Status", jobExecution.getExitStatus());
        assertNotNull(jobExecution.getCreateTime());
        assertNotNull(jobExecution.getLastUpdatedTime());
        assertNotNull(jobExecution.getStartTime());
        assertNotNull(jobExecution.getEndTime());
        assertEquals("Exit Status", jobExecution.getRestartElementId());
        final Properties params = jobExecution.getJobParameters();
        assertNotNull(params);
        assertEquals("value", params.getProperty("test"));
        assertEquals(1, params.stringPropertyNames().size());

        try {
            repository().finishJobExecution(
                    Long.MIN_VALUE,
                    BatchStatus.COMPLETED,
                    "Exit Status",
                    "Exit Status",
                    new Date()
            );
            fail();
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
        final Step<?,?> step1 = Step.class.cast(job.getExecutions().get(0));
        final Step<?,?> step2 = Step.class.cast(job.getExecutions().get(1));
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        ExtendedJobExecution first = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        Thread.sleep(1);
        ExtendedStepExecution firstS1 = repository().createStepExecution(
                first,
                step1.getId(),
                new Date()
        );
        Thread.sleep(1);
        assertEquals(
                firstS1.getStepExecutionId(),
                repository().getLatestStepExecution(first.getExecutionId(), step1.getId()).getStepExecutionId()
        );
        try {
            repository().getPreviousStepExecution(first.getExecutionId(), firstS1.getStepExecutionId(), step1.getId());
            fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
        ExtendedStepExecution firstS2 = repository().createStepExecution(
                first,
                step2.getId(),
                new Date()
        );
        Thread.sleep(1);
        assertEquals(
                firstS2.getStepExecutionId(),
                repository().getLatestStepExecution(first.getExecutionId(), step2.getId()).getStepExecutionId()
        );
        /*
        assertEquals(
                firstS1.getStepExecutionId(),
                repository().getPreviousStepExecution(first.getExecutionId(), firstS2.getStepExecutionId(), step1.getId()).getStepExecutionId()
        );
        */
        repository().finishJobExecution(
                first.getExecutionId(),
                BatchStatus.COMPLETED,
                BatchStatus.COMPLETED.name(),
                null,
                new Date()
        );
        assertEquals(0, repository().getStepExecutionCount(first.getExecutionId(), step1.getId()));
        assertEquals(0, repository().getStepExecutionCount(first.getExecutionId(), step2.getId()));

        ExtendedJobExecution second = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        Thread.sleep(1);
        ExtendedStepExecution secondS1 = repository().createStepExecution(
                second,
                step1.getId(),
                new Date()
        );
        Thread.sleep(1);
        assertEquals(
                secondS1.getStepExecutionId(),
                repository().getLatestStepExecution(second.getExecutionId(), step1.getId()).getStepExecutionId()
        );
        try {
            repository().getPreviousStepExecution(second.getExecutionId(), secondS1.getStepExecutionId(), step1.getId());
            fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
        ExtendedStepExecution secondS2 = repository().createStepExecution(
                second,
                step2.getId(),
                new Date()
        );
        Thread.sleep(1);
        assertEquals(
                secondS2.getStepExecutionId(),
                repository().getLatestStepExecution(second.getExecutionId(), step2.getId()).getStepExecutionId()
        );
        /*
        assertEquals(
                secondS1.getStepExecutionId(),
                repository().getPreviousStepExecution(second.getExecutionId(), secondS2.getStepExecutionId(), step1.getId()).getStepExecutionId()
        );
        */
        repository().finishJobExecution(
                second.getExecutionId(),
                BatchStatus.COMPLETED,
                BatchStatus.COMPLETED.name(),
                null,
                new Date()
        );
        assertEquals(0, repository().getStepExecutionCount(second.getExecutionId(), step1.getId()));
        assertEquals(0, repository().getStepExecutionCount(second.getExecutionId(), step2.getId()));

        ExtendedJobExecution third = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        Thread.sleep(1);
        ExtendedStepExecution thirdS1 = repository().createStepExecution(
                third,
                step1.getId(),
                new Date()
        );
        Thread.sleep(1);
        assertEquals(
                thirdS1.getStepExecutionId(),
                repository().getLatestStepExecution(third.getExecutionId(), step1.getId()).getStepExecutionId()
        );
        try {
            repository().getPreviousStepExecution(third.getExecutionId(), thirdS1.getStepExecutionId(), step1.getId());
            fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
        ExtendedStepExecution thirdS2 = repository().createStepExecution(
                third,
                step2.getId(),
                new Date()
        );
        assertEquals(
                thirdS2.getStepExecutionId(),
                repository().getLatestStepExecution(third.getExecutionId(), step2.getId()).getStepExecutionId()
        );
        /*
        assertEquals(
                thirdS1.getStepExecutionId(),
                repository().getPreviousStepExecution(third.getExecutionId(), thirdS2.getStepExecutionId(), step1.getId()).getStepExecutionId()
        );
        */
        repository().finishJobExecution(
                third.getExecutionId(),
                BatchStatus.COMPLETED,
                BatchStatus.COMPLETED.name(),
                null,
                new Date()
        );
        assertEquals(0, repository().getStepExecutionCount(third.getExecutionId(), step1.getId()));
        assertEquals(0, repository().getStepExecutionCount(third.getExecutionId(), step2.getId()));

        repository().linkJobExecutions(
                second.getExecutionId(),
                first.getExecutionId()
        );
        assertEquals(
                firstS1.getStepExecutionId(),
                repository().getPreviousStepExecution(second.getExecutionId(), secondS1.getStepExecutionId(), step1.getId()).getStepExecutionId()
        );
        assertEquals(
                firstS2.getStepExecutionId(),
                repository().getPreviousStepExecution(second.getExecutionId(), secondS2.getStepExecutionId(), step2.getId()).getStepExecutionId()
        );
        assertEquals(1, repository().getStepExecutionCount(second.getExecutionId(), step1.getId()));
        assertEquals(1, repository().getStepExecutionCount(second.getExecutionId(), step2.getId()));

        repository().linkJobExecutions(
                third.getExecutionId(),
                second.getExecutionId()
        );
        assertEquals(
                secondS1.getStepExecutionId(),
                repository().getPreviousStepExecution(third.getExecutionId(), thirdS1.getStepExecutionId(), step1.getId()).getStepExecutionId()
        );
        assertEquals(
                secondS2.getStepExecutionId(),
                repository().getPreviousStepExecution(third.getExecutionId(), thirdS2.getStepExecutionId(), step2.getId()).getStepExecutionId()
        );
        assertEquals(2, repository().getStepExecutionCount(third.getExecutionId(), step1.getId()));
        assertEquals(2, repository().getStepExecutionCount(third.getExecutionId(), step2.getId()));
    }

    @Test
    public void startStepExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final Step<?,?> step1 = Step.class.cast(job.getExecutions().get(0));
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                step1.getId(),
                new Date()
        );

        assertEquals(step1.getId(), stepExecution.getStepName());
        assertEquals(BatchStatus.STARTING, stepExecution.getBatchStatus());
        assertNull(stepExecution.getExitStatus());
        assertNotNull(stepExecution.getCreateTime());
        assertNotNull(stepExecution.getUpdatedTime());
        assertNull(stepExecution.getStartTime());
        assertNull(stepExecution.getEndTime());
        assertNull(stepExecution.getPersistentUserData());
        assertNull(stepExecution.getReaderCheckpoint());
        assertNull(stepExecution.getWriterCheckpoint());

        repository().startStepExecution(
                stepExecution.getStepExecutionId(),
                new Date()
        );
        stepExecution = repository().getStepExecution(stepExecution.getStepExecutionId());

        assertEquals(step1.getId(), stepExecution.getStepName());
        assertEquals(BatchStatus.STARTED, stepExecution.getBatchStatus());
        assertNull(stepExecution.getExitStatus());
        assertNotNull(stepExecution.getCreateTime());
        assertNotNull(stepExecution.getUpdatedTime());
        assertNotNull(stepExecution.getStartTime());
        assertNull(stepExecution.getEndTime());
        assertNull(stepExecution.getPersistentUserData());
        assertNull(stepExecution.getReaderCheckpoint());
        assertNull(stepExecution.getWriterCheckpoint());

        _isEmptyMetrics(stepExecution.getMetrics());
    }

    @Test
    public void updateStepExecutionTest() throws Exception {
        printMethodName();

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final Step<?,?> step1 = Step.class.cast(job.getExecutions().get(0));
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                step1.getId(),
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

        assertEquals(step1.getId(), stepExecution.getStepName());
        assertEquals(BatchStatus.STARTING, stepExecution.getBatchStatus());
        assertNull(stepExecution.getExitStatus());
        assertNotNull(stepExecution.getCreateTime());
        assertNotNull(stepExecution.getUpdatedTime());
        assertNull(stepExecution.getStartTime());
        assertNull(stepExecution.getEndTime());
        assertEquals(persist, nextSerial);
        assertFalse(persist == nextSerial);
        assertNull(stepExecution.getReaderCheckpoint());
        assertNull(stepExecution.getWriterCheckpoint());

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

        assertEquals(step1.getId(), stepExecution.getStepName());
        assertEquals(BatchStatus.STARTED, stepExecution.getBatchStatus());
        assertNull(stepExecution.getExitStatus());
        assertNotNull(stepExecution.getCreateTime());
        assertNotNull(stepExecution.getUpdatedTime());
        assertNotNull(stepExecution.getStartTime());
        assertNull(stepExecution.getEndTime());
        assertEquals(nextSerial, thirdSerial);
        assertFalse(nextSerial == thirdSerial);
        assertNull(stepExecution.getReaderCheckpoint());
        assertNull(stepExecution.getWriterCheckpoint());

        _isSameMetrics(metrics, stepExecution.getMetrics());

        try {
            repository().updateStepExecution(
                    Long.MIN_VALUE,
                    new Metric[0],
                    "",
                    new Date()
            );
            fail();
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
        final Step<?,?> step1 = Step.class.cast(job.getExecutions().get(0));
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                step1.getId(),
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

        assertEquals(step1.getId(), stepExecution.getStepName());
        assertEquals(BatchStatus.STARTING, stepExecution.getBatchStatus());
        assertNull(stepExecution.getExitStatus());
        assertNotNull(stepExecution.getCreateTime());
        assertNotNull(stepExecution.getUpdatedTime());
        assertNull(stepExecution.getStartTime());
        assertNull(stepExecution.getEndTime());
        assertNotNull(nextPersist);
        assertNotNull(nextReader);
        assertNotNull(nextWriter);
        assertEquals(persist, nextPersist);
        assertFalse(persist == nextPersist);
        assertEquals(reader, nextReader);
        assertFalse(reader == nextReader);
        assertEquals(writer, nextWriter);
        assertFalse(writer == nextWriter);

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

        assertEquals(step1.getId(), stepExecution.getStepName());
        assertEquals(BatchStatus.STARTED, stepExecution.getBatchStatus());
        assertNull(stepExecution.getExitStatus());
        assertNotNull(stepExecution.getCreateTime());
        assertNotNull(stepExecution.getUpdatedTime());
        assertNotNull(stepExecution.getStartTime());
        assertNull(stepExecution.getEndTime());
        assertNotNull(thirdPersist);
        assertNotNull(thirdReader);
        assertNotNull(thirdWriter);
        assertEquals(nextPersist, thirdPersist);
        assertFalse(nextPersist == thirdPersist);
        assertEquals(nextReader, thirdReader);
        assertFalse(nextReader == thirdReader);
        assertEquals(nextWriter, thirdWriter);
        assertFalse(nextWriter == thirdWriter);

        _isSameMetrics(metrics, stepExecution.getMetrics());

        try {
            repository().updateStepExecution(
                    Long.MIN_VALUE,
                    new Metric[0],
                    "",
                    new Date()
            );
            fail();
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
        final Step<?,?> step1 = Step.class.cast(job.getExecutions().get(0));
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        final ExtendedJobExecution jobExecution = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        ExtendedStepExecution stepExecution = repository().createStepExecution(
                jobExecution,
                step1.getId(),
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

        assertEquals(step1.getId(), stepExecution.getStepName());
        assertEquals(BatchStatus.FAILED, stepExecution.getBatchStatus());
        assertEquals("some", stepExecution.getExitStatus());
        assertNotNull(stepExecution.getCreateTime());
        assertNotNull(stepExecution.getUpdatedTime());
        assertNotNull(stepExecution.getStartTime());
        assertNotNull(stepExecution.getEndTime());
        assertNull(stepExecution.getPersistentUserData());
        assertNull(stepExecution.getReaderCheckpoint());
        assertNull(stepExecution.getWriterCheckpoint());

        _isSameMetrics(metrics, stepExecution.getMetrics());

        try {
            repository().finishStepExecution(
                    Long.MIN_VALUE,
                    new Metric[0],
                    BatchStatus.FAILED,
                    "",
                    new Date()
            );
            fail();
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

        assertEquals(stepExecution.getStepExecutionId(), partition.getStepExecutionId());
        assertEquals(4, partition.getPartitionId());
        assertEquals(BatchStatus.STARTED, partition.getBatchStatus());
        assertNull(partition.getExitStatus());
        assertNotNull(partition.getCreateTime());
        assertNotNull(partition.getUpdatedTime());
        assertNotNull(partition.getStartTime());
        assertNull(partition.getEndTime());
        assertNull(partition.getPersistentUserData());
        assertNull(partition.getReaderCheckpoint());
        assertNull(partition.getWriterCheckpoint());

        _isEmptyMetrics(partition.getMetrics());

        final Properties params = partition.getPartitionParameters();
        assertNotNull(params);
        assertEquals("value", params.getProperty("test"));
        assertEquals(1, params.stringPropertyNames().size());


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

        assertEquals(stepExecution.getStepExecutionId(), otherPartition.getStepExecutionId());
        assertEquals(4, otherPartition.getPartitionId());
        assertEquals(BatchStatus.STARTED, otherPartition.getBatchStatus());
        assertNull(otherPartition.getExitStatus());
        assertNotNull(otherPartition.getCreateTime());
        assertNotNull(otherPartition.getUpdatedTime());
        assertNotNull(otherPartition.getStartTime());
        assertNull(otherPartition.getEndTime());
        assertNull(otherPartition.getPersistentUserData());
        assertNull(otherPartition.getReaderCheckpoint());
        assertNull(otherPartition.getWriterCheckpoint());

        _isEmptyMetrics(otherPartition.getMetrics());

        final Properties otherParams = otherPartition.getPartitionParameters();
        assertNotNull(otherParams);
        assertEquals("value", otherParams.getProperty("test"));
        assertEquals(1, otherParams.stringPropertyNames().size());

        try {
            repository().startPartitionExecution(
                    Long.MIN_VALUE,
                    new Date()
            );
            fail();
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

        assertEquals(stepExecution.getStepExecutionId(), partition.getStepExecutionId());
        assertEquals(4, partition.getPartitionId());
        assertEquals(BatchStatus.STARTING, partition.getBatchStatus());
        assertNull(partition.getExitStatus());
        assertNotNull(partition.getCreateTime());
        assertNotNull(partition.getUpdatedTime());
        assertNull(partition.getStartTime());
        assertNull(partition.getEndTime());
        assertNotNull(nextPersist);
        assertNotNull(nextReader);
        assertNotNull(nextWriter);
        assertEquals(persist, nextPersist);
        assertFalse(persist == nextPersist);
        assertEquals(reader, nextReader);
        assertFalse(reader == nextReader);
        assertEquals(writer, nextWriter);
        assertFalse(writer == nextWriter);

        _isSameMetrics(metrics, partition.getMetrics());

        final Properties params = partition.getPartitionParameters();
        assertNotNull(params);
        assertEquals("value", params.getProperty("test"));
        assertEquals(1, params.stringPropertyNames().size());

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

        assertEquals(BatchStatus.STARTED, partition.getBatchStatus());
        assertNull(partition.getExitStatus());
        assertNotNull(partition.getCreateTime());
        assertNotNull(partition.getUpdatedTime());
        assertNotNull(partition.getStartTime());
        assertNull(partition.getEndTime());
        assertNotNull(thirdPersist);
        assertNotNull(thirdReader);
        assertNotNull(thirdWriter);
        assertEquals(nextPersist, thirdPersist);
        assertFalse(nextPersist == thirdPersist);
        assertEquals(nextReader, thirdReader);
        assertFalse(nextReader == thirdReader);
        assertEquals(nextWriter, thirdWriter);
        assertFalse(nextWriter == thirdWriter);

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

        assertEquals(stepExecution.getStepExecutionId(), otherPartition.getStepExecutionId());
        assertEquals(4, otherPartition.getPartitionId());
        assertEquals(BatchStatus.STARTING, otherPartition.getBatchStatus());
        assertNull(otherPartition.getExitStatus());
        assertNotNull(otherPartition.getCreateTime());
        assertNotNull(otherPartition.getUpdatedTime());
        assertNull(otherPartition.getStartTime());
        assertNull(otherPartition.getEndTime());
        assertNotNull(otherNextPersist);
        assertNotNull(otherNextReader);
        assertNotNull(otherNextWriter);
        assertEquals(persist, otherNextPersist);
        assertFalse(persist == otherNextPersist);
        assertEquals(reader, otherNextReader);
        assertFalse(reader == otherNextReader);
        assertEquals(writer, otherNextWriter);
        assertFalse(writer == otherNextWriter);

        _isSameMetrics(metrics, otherPartition.getMetrics());

        final Properties otherParams = otherPartition.getPartitionParameters();
        assertNotNull(otherParams);
        assertEquals("value", otherParams.getProperty("test"));
        assertEquals(1, otherParams.stringPropertyNames().size());

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

        assertEquals(BatchStatus.STARTED, otherPartition.getBatchStatus());
        assertNull(otherPartition.getExitStatus());
        assertNotNull(otherPartition.getCreateTime());
        assertNotNull(otherPartition.getUpdatedTime());
        assertNotNull(otherPartition.getStartTime());
        assertNull(otherPartition.getEndTime());
        assertNotNull(otherThirdPersist);
        assertNotNull(otherThirdReader);
        assertNotNull(otherThirdWriter);
        assertEquals(otherNextPersist, otherThirdPersist);
        assertFalse(otherNextPersist == otherThirdPersist);
        assertEquals(otherNextReader, otherThirdReader);
        assertFalse(otherNextReader == otherThirdReader);
        assertEquals(otherNextWriter, otherThirdWriter);
        assertFalse(otherNextWriter == otherThirdWriter);

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
            fail();
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

        assertEquals(stepExecution.getStepExecutionId(), partition.getStepExecutionId());
        assertEquals(4, partition.getPartitionId());
        assertEquals(BatchStatus.FAILED, partition.getBatchStatus());
        assertEquals(exitStatus, partition.getExitStatus());
        assertNotNull(partition.getCreateTime());
        assertNotNull(partition.getUpdatedTime());
        assertNull(partition.getStartTime());
        assertNotNull(partition.getEndTime());
        assertNotNull(nextPersist);
        assertNull(partition.getReaderCheckpoint());
        assertNull(partition.getWriterCheckpoint());
        assertEquals(persist, nextPersist);
        assertFalse(persist == nextPersist);

        _isSameMetrics(metrics, partition.getMetrics());

        final Properties params = partition.getPartitionParameters();
        assertNotNull(params);
        assertEquals("value", params.getProperty("test"));
        assertEquals(1, params.stringPropertyNames().size());

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

        assertEquals(stepExecution.getStepExecutionId(), partition.getStepExecutionId());
        assertEquals(5, partition.getPartitionId());
        assertEquals(BatchStatus.COMPLETED, partition.getBatchStatus());
        assertEquals(exitStatus, partition.getExitStatus());
        assertNotNull(partition.getCreateTime());
        assertNotNull(partition.getUpdatedTime());
        assertNotNull(partition.getStartTime());
        assertNotNull(partition.getEndTime());
        assertNotNull(thirdPersist);
        assertNull(partition.getReaderCheckpoint());
        assertNull(partition.getWriterCheckpoint());
        assertEquals(nextPersist, thirdPersist);
        assertFalse(nextPersist == thirdPersist);

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

        assertEquals(stepExecution.getStepExecutionId(), otherPartition.getStepExecutionId());
        assertEquals(5, otherPartition.getPartitionId());
        assertEquals(BatchStatus.STOPPED, otherPartition.getBatchStatus());
        assertEquals(otherExitStatus, otherPartition.getExitStatus());
        assertNotNull(otherPartition.getCreateTime());
        assertNotNull(otherPartition.getUpdatedTime());
        assertNull(otherPartition.getStartTime());
        assertNotNull(otherPartition.getEndTime());
        assertNotNull(otherNextPersist);
        assertNull(otherPartition.getReaderCheckpoint());
        assertNull(otherPartition.getWriterCheckpoint());
        assertEquals(persist, otherNextPersist);
        assertFalse(persist == otherNextPersist);

        _isSameMetrics(metrics, otherPartition.getMetrics());

        final Properties otherParams = otherPartition.getPartitionParameters();
        assertNotNull(otherParams);
        assertEquals("value", otherParams.getProperty("test"));
        assertEquals(1, otherParams.stringPropertyNames().size());

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

        assertEquals(stepExecution.getStepExecutionId(), otherPartition.getStepExecutionId());
        assertEquals(5, otherPartition.getPartitionId());
        assertEquals(BatchStatus.COMPLETED, otherPartition.getBatchStatus());
        assertEquals(exitStatus, otherPartition.getExitStatus());
        assertNotNull(otherPartition.getCreateTime());
        assertNotNull(otherPartition.getUpdatedTime());
        assertNotNull(otherPartition.getStartTime());
        assertNotNull(otherPartition.getEndTime());
        assertNotNull(otherThirdPersist);
        assertNull(otherPartition.getReaderCheckpoint());
        assertNull(otherPartition.getWriterCheckpoint());
        assertEquals(otherNextPersist, otherThirdPersist);
        assertFalse(otherNextPersist == otherThirdPersist);

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
            fail();
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

        final Properties parameters = new Properties();
        parameters.put("test", "value");

        final JobImpl job = _job();
        final Step<?,?> step1 = Step.class.cast(job.getExecutions().get(0));
        final ExtendedJobInstance jobInstance = repository().createJobInstance(job.getId(), "jsl", new Date());
        ExtendedJobExecution first = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        Thread.sleep(1);
        ExtendedStepExecution firstS1 = repository().createStepExecution(
                first,
                step1.getId(),
                new Date()
        );
        Thread.sleep(1);
        repository().finishJobExecution(
                firstS1.getJobExecutionId(),
                BatchStatus.COMPLETED,
                BatchStatus.COMPLETED.name(),
                null,
                new Date()
        );

        ExtendedStepExecution firstS2 = repository().createStepExecution(
                first,
                step1.getId(),
                new Date()
        );
        Thread.sleep(1);
        assertEquals(
                firstS2.getStepExecutionId(),
                repository().getLatestStepExecution(first.getExecutionId(), step1.getId()).getStepExecutionId()
        );
        assertEquals(
                firstS1.getStepExecutionId(),
                repository().getPreviousStepExecution(first.getExecutionId(), firstS2.getStepExecutionId(), step1.getId()).getStepExecutionId()
        );

        ExtendedJobExecution second = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        Thread.sleep(1);
        ExtendedStepExecution secondS1 = repository().createStepExecution(
                second,
                step1.getId(),
                new Date()
        );
        Thread.sleep(1);
        assertEquals(
                secondS1.getStepExecutionId(),
                repository().getLatestStepExecution(second.getExecutionId(), step1.getId()).getStepExecutionId()
        );
        try {
            repository().getPreviousStepExecution(second.getExecutionId(), secondS1.getStepExecutionId(), step1.getId());
            fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
        ExtendedStepExecution secondS2 = repository().createStepExecution(
                second,
                step1.getId(),
                new Date()
        );
        Thread.sleep(1);
        assertEquals(
                secondS2.getStepExecutionId(),
                repository().getLatestStepExecution(second.getExecutionId(), step1.getId()).getStepExecutionId()
        );
        assertEquals(
                secondS1.getStepExecutionId(),
                repository().getPreviousStepExecution(second.getExecutionId(), secondS2.getStepExecutionId(), step1.getId()).getStepExecutionId()
        );

        ExtendedJobExecution third = repository().createJobExecution(
                jobInstance,
                parameters,
                new Date()
        );
        Thread.sleep(1);
        ExtendedStepExecution thirdS1 = repository().createStepExecution(
                third,
                step1.getId(),
                new Date()
        );
        Thread.sleep(1);
        assertEquals(
                thirdS1.getStepExecutionId(),
                repository().getLatestStepExecution(third.getExecutionId(), step1.getId()).getStepExecutionId()
        );
        try {
            repository().getPreviousStepExecution(third.getExecutionId(), thirdS1.getStepExecutionId(), step1.getId());
            fail();
        } catch (NoSuchJobExecutionException e) {
            //
        }
        ExtendedStepExecution thirdS2 = repository().createStepExecution(
                third,
                step1.getId(),
                new Date()
        );
        assertEquals(
                thirdS2.getStepExecutionId(),
                repository().getLatestStepExecution(third.getExecutionId(), step1.getId()).getStepExecutionId()
        );
        assertEquals(
                thirdS1.getStepExecutionId(),
                repository().getPreviousStepExecution(third.getExecutionId(), thirdS2.getStepExecutionId(), step1.getId()).getStepExecutionId()
        );

        repository().linkJobExecutions(
                second.getExecutionId(),
                first.getExecutionId()
        );
        assertEquals(
                firstS2.getStepExecutionId(),
                repository().getPreviousStepExecution(second.getExecutionId(), secondS1.getStepExecutionId(), step1.getId()).getStepExecutionId()
        );

        repository().linkJobExecutions(
                third.getExecutionId(),
                second.getExecutionId()
        );
        assertEquals(
                secondS2.getStepExecutionId(),
                repository().getPreviousStepExecution(third.getExecutionId(), thirdS1.getStepExecutionId(), step1.getId()).getStepExecutionId()
        );
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
        assertNotNull(metrics);
        assertEquals(8, metrics.length);
        int seen = 0;
        for (final Metric metric : metrics) {
            assertNotNull(metric);
            switch (metric.getType()) {
                case READ_COUNT:
                    seen += 3;
                    assertEquals(0, metric.getValue());
                    break;
                case WRITE_COUNT:
                    seen += 5;
                    assertEquals(0, metric.getValue());
                    break;
                case COMMIT_COUNT:
                    seen += 7;
                    assertEquals(0, metric.getValue());
                    break;
                case ROLLBACK_COUNT:
                    seen += 11;
                    assertEquals(0, metric.getValue());
                    break;
                case READ_SKIP_COUNT:
                    seen += 13;
                    assertEquals(0, metric.getValue());
                    break;
                case PROCESS_SKIP_COUNT:
                    seen += 17;
                    assertEquals(0, metric.getValue());
                    break;
                case FILTER_COUNT:
                    seen += 19;
                    assertEquals(0, metric.getValue());
                    break;
                case WRITE_SKIP_COUNT:
                    seen += 23;
                    assertEquals(0, metric.getValue());
                    break;
                default:
                    fail(String.valueOf(metric.getType()));
            }
        }
        assertEquals(98, seen);
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
        assertNotNull(source);
        assertEquals(8, source.length);

        int seen = 0;
        outer: for (final Metric metric : source) {
            switch (metric.getType()) {
                case READ_COUNT:
                    seen += 3;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.READ_COUNT) {
                            assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    fail();
                case WRITE_COUNT:
                    seen += 5;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.WRITE_COUNT) {
                            assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    fail();
                case COMMIT_COUNT:
                    seen += 7;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.COMMIT_COUNT) {
                            assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    fail();
                case ROLLBACK_COUNT:
                    seen += 11;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.ROLLBACK_COUNT) {
                            assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    fail();
                case READ_SKIP_COUNT:
                    seen += 13;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.READ_SKIP_COUNT) {
                            assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    fail();
                case PROCESS_SKIP_COUNT:
                    seen += 17;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.PROCESS_SKIP_COUNT) {
                            assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    fail();
                case FILTER_COUNT:
                    seen += 19;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.FILTER_COUNT) {
                            assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    fail();
                case WRITE_SKIP_COUNT:
                    seen += 23;
                    for (final Metric m : target) {
                        if (m.getType() == Metric.MetricType.WRITE_SKIP_COUNT) {
                            assertEquals(metric.getValue(), m.getValue());
                            continue outer;
                        }
                    }
                    fail();
                default:
                    fail(String.valueOf(metric.getType()));
            }
        }
        assertEquals(98, seen);
    }
}
