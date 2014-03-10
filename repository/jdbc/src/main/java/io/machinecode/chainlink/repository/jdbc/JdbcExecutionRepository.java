package io.machinecode.chainlink.repository.jdbc;

import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.element.execution.Step;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JdbcExecutionRepository implements ExecutionRepository {

    private final DataSource dataSource;

    public JdbcExecutionRepository(final DataSourceLookup lookup) {
        this.dataSource = lookup.getDataSource();
    }

    @Override
    public ExtendedJobInstance createJobInstance(final Job job, final String jslName, final Date timestamp) throws Exception {
        final Connection connection = dataSource.getConnection();
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ExtendedJobExecution createJobExecution(final ExtendedJobInstance jobInstance, final Properties parameters, final Date timestamp) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ExtendedStepExecution createStepExecution(final JobExecution jobExecution, final Step<?, ?> step, final Date timestamp) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Date timestamp) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final PartitionExecution partitionExecution, final Date timestamp) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void startJobExecution(final long jobExecutionId, final Date timestamp) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void linkJobExecutions(final long jobExecutionId, final ExtendedJobExecution restartJobExecution) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Date timestamp) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<String> getJobNames() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws Exception {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobInstanceId) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<? extends JobExecution> getJobExecutions(final JobInstance instance) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<? extends StepExecution> getStepExecutionsForJob(final long jobExecutionId) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception {
        return new StepExecution[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception {
        return new PartitionExecution[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
