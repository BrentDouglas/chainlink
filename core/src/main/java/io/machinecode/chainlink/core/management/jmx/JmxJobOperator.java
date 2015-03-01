package io.machinecode.chainlink.core.management.jmx;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.management.JobOperation;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import javax.batch.operations.BatchRuntimeException;
import javax.batch.operations.JobExecutionAlreadyCompleteException;
import javax.batch.operations.JobExecutionIsRunningException;
import javax.batch.operations.JobExecutionNotMostRecentException;
import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.JobRestartException;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.JobStartException;
import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.OpenDataException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JmxJobOperator implements ExtendedJobOperator {

    final Configuration configuration;
    private JmxJobOperatorClient client;

    public JmxJobOperator(final Configuration configuration, final ObjectName name) {
        this.configuration = configuration;
        this.client = new JmxJobOperatorClient(configuration.getMBeanServer(), name);
    }

    @Override
    public void close() throws Exception {
        //
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        //
    }

    @Override
    public Set<String> getJobNames() throws JobSecurityException {
        try {
            return client.getJobNames();
        } catch (final AttributeNotFoundException | MBeanException | ReflectionException | InstanceNotFoundException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException {
        try {
            return client.getJobInstanceCount(jobName);
        } catch (final MBeanException | ReflectionException | InstanceNotFoundException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException {
        try {
            return JmxUtils.readJobInstances(client.getJobInstances(jobName, start, count));
        } catch (final OpenDataException | MBeanException | ReflectionException | InstanceNotFoundException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException {
        try {
            return client.getRunningExecutions(jobName);
        } catch (final MBeanException | ReflectionException | InstanceNotFoundException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        try {
            return JmxUtils.readProperties(client.getParameters(jobExecutionId));
        } catch (final OpenDataException | MBeanException | ReflectionException | InstanceNotFoundException e) {
            throw new BatchRuntimeException(e);
        }
    }

    String params(final Properties parameters) {
        final StringBuilder builder = new StringBuilder("#");
        for (final String key : parameters.stringPropertyNames()) {
            builder.append("\n").append(key).append("=").append(parameters.getProperty(key));
        }
        return builder.toString();
    }

    @Override
    public long start(final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        try {
            return client.start(jslName, params(parameters));
        } catch (final MBeanException | ReflectionException | InstanceNotFoundException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public long restart(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        try {
            return client.restart(jobExecutionId, params(parameters));
        } catch (final MBeanException | ReflectionException | InstanceNotFoundException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public void stop(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        try {
            client.stop(jobExecutionId);
        } catch (final MBeanException | ReflectionException | InstanceNotFoundException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public void abandon(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        try {
            client.abandon(jobExecutionId);
        } catch (final MBeanException | ReflectionException | InstanceNotFoundException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        try {
            return JmxUtils.readExtendedJobInstance(client.getJobInstance(jobExecutionId));
        } catch (final OpenDataException | MBeanException | ReflectionException | InstanceNotFoundException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance jobInstance) throws NoSuchJobInstanceException, JobSecurityException {
        try {
            return JmxUtils.readJobExecutions(client.getJobExecutions(jobInstance.getInstanceId()));
        } catch (final OpenDataException | MBeanException | ReflectionException | InstanceNotFoundException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        try {
            return JmxUtils.readExtendedJobExecution(client.getJobExecution(jobExecutionId));
        } catch (final OpenDataException | MBeanException | ReflectionException | InstanceNotFoundException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        try {
            return JmxUtils.readStepExecutions(client.getStepExecutions(jobExecutionId), configuration.getMarshalling(), configuration.getClassLoader());
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstanceById(final long jobInstanceId) {
        try {
            return JmxUtils.readExtendedJobInstance(client.getJobInstanceById(jobInstanceId));
        } catch (final OpenDataException | MBeanException | ReflectionException | InstanceNotFoundException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public JobOperation startJob(final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public JobOperation getJobOperation(final long jobExecutionId) throws JobExecutionNotRunningException {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public JobOperation restartJob(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public Future<?> stopJob(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        throw new IllegalStateException("Not implemented");
    }
}
