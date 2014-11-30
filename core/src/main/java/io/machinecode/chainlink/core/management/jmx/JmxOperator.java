package io.machinecode.chainlink.core.management.jmx;

import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;

import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JmxOperator implements JmxOperatorMBean {

    private static final AtomicLong SESSION_IDS = new AtomicLong(0);

    private final ExtendedJobOperator operator;
    private final MBeanServer server;
    private final String domain;

    public JmxOperator(final ExtendedJobOperator operator, final MBeanServer server, final String domain) {
        this.operator = operator;
        this.server = server;
        this.domain = domain;
    }

    @Override
    public void startSession() throws Exception {
        final ObjectName name = new ObjectName(domain + ":type=Session,name=JmxSession,id=" + SESSION_IDS.incrementAndGet());
        server.registerMBean(
                new JmxSession(operator, server, name),
                name
        );
    }

    @Override
    public Set<String> getJobNames() {
        return operator.getJobNames();
    }

    @Override
    public int getJobInstanceCount(final String jobName) {
        return operator.getJobInstanceCount(jobName);
    }

    @Override
    public List<Properties> getJobInstances(final String jobName, final int start, final int count) {
        final List<JobInstance> jobInstances = operator.getJobInstances(jobName, start, count);
        final List<Properties> ret = new ArrayList<Properties>(jobInstances.size());
        final SimpleDateFormat format = new SimpleDateFormat(); //TODO
        for (final JobInstance jobInstance : jobInstances) {
            final Properties properties = new Properties();
            ret.add(properties);
            _jobInstance(jobInstance, properties, format);
        }
        return ret;
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) {
        return operator.getRunningExecutions(jobName);
    }

    @Override
    public Properties getParameters(final long jobExecutionId) {
        return operator.getJobExecution(jobExecutionId).getJobParameters();
    }

    @Override
    public Properties getJobInstance(final long jobExecutionId) {
        final JobInstance jobInstance = operator.getJobInstance(jobExecutionId);
        final SimpleDateFormat format = new SimpleDateFormat(); //TODO
        final Properties properties = new Properties();
        _jobInstance(jobInstance, properties, format);
        return properties;
    }

    @Override
    public List<Properties> getJobExecutions(final long jobInstanceId) {
        final JobInstance jobInstance = operator.getJobInstanceById(jobInstanceId);
        final List<JobExecution> jobExecutions = operator.getJobExecutions(jobInstance);
        final List<Properties> ret = new ArrayList<Properties>(jobExecutions.size());
        final SimpleDateFormat format = new SimpleDateFormat(); //TODO
        for (final JobExecution jobExecution : jobExecutions) {
            final Properties properties = new Properties();
            ret.add(properties);
            _jobExecution(jobExecution, properties, format);
        }
        return ret;
    }

    @Override
    public Properties getJobExecution(final long jobExecutionId) {
        final JobExecution jobExecution = operator.getJobExecution(jobExecutionId);
        final SimpleDateFormat format = new SimpleDateFormat(); //TODO
        final Properties properties = new Properties();
        _jobExecution(jobExecution, properties, format);
        return properties;
    }

    @Override
    public List<Properties> getStepExecutions(final long jobExecutionId) {
        final List<StepExecution> stepExecutions = operator.getStepExecutions(jobExecutionId);
        final List<Properties> ret = new ArrayList<Properties>(stepExecutions.size());
        final SimpleDateFormat format = new SimpleDateFormat(); //TODO
        for (final StepExecution stepExecution : stepExecutions) {
            final Properties properties = new Properties();
            ret.add(properties);
            _stepExecution(stepExecution, properties, format);
        }
        return ret;
    }

    private void _parameters(final Properties parameters, final Properties properties) {
        for (final String key : parameters.stringPropertyNames()) {
            properties.setProperty("parameter->" + key, parameters.getProperty(key));
        }
    }

    private void _jobInstance(final JobInstance jobInstance, final Properties properties, final SimpleDateFormat format) {
        properties.setProperty("jobInstanceId", Long.toString(jobInstance.getInstanceId()));
        properties.setProperty("jobName", jobInstance.getJobName());
        if (jobInstance instanceof ExtendedJobInstance) {
            final ExtendedJobInstance extendedJobInstance = (ExtendedJobInstance) jobInstance;
            properties.setProperty("jslName", extendedJobInstance.getJslName());
            properties.setProperty("createTime", extendedJobInstance.getCreateTime() == null ? null : format.format(extendedJobInstance.getCreateTime()));
        }
    }

    private void _jobExecution(final JobExecution jobExecution, final Properties properties, final SimpleDateFormat format) {
        properties.setProperty("jobExecutionId", Long.toString(jobExecution.getExecutionId()));
        properties.setProperty("jobName", jobExecution.getJobName());
        properties.setProperty("batchStatus", jobExecution.getBatchStatus().toString());
        properties.setProperty("createTime", jobExecution.getCreateTime() == null ? null : format.format(jobExecution.getCreateTime()));
        properties.setProperty("startTime", jobExecution.getStartTime() == null ? null : format.format(jobExecution.getStartTime()));
        properties.setProperty("endTime", jobExecution.getEndTime() == null ? null : format.format(jobExecution.getEndTime()));
        properties.setProperty("lastUpdatedTime", jobExecution.getLastUpdatedTime() == null ? null : format.format(jobExecution.getLastUpdatedTime()));
        properties.setProperty("exitStatus", jobExecution.getExitStatus());
        if (jobExecution instanceof ExtendedJobExecution) {
            final ExtendedJobExecution extendedJobExecution = (ExtendedJobExecution) jobExecution;
            properties.setProperty("restartElementId", extendedJobExecution.getRestartElementId());
            properties.setProperty("jobInstanceId", Long.toString(extendedJobExecution.getJobInstanceId()));
        }
        _parameters(jobExecution.getJobParameters(), properties);
    }

    private void _stepExecution(final StepExecution stepExecution, final Properties properties, final SimpleDateFormat format) {
        properties.setProperty("stepExecutionId", Long.toString(stepExecution.getStepExecutionId()));
        properties.setProperty("stepName", stepExecution.getStepName());
        properties.setProperty("batchStatus", stepExecution.getBatchStatus().toString());
        properties.setProperty("startTime", stepExecution.getStartTime() == null ? null : format.format(stepExecution.getStartTime()));
        properties.setProperty("endTime", stepExecution.getEndTime() == null ? null : format.format(stepExecution.getEndTime()));
        properties.setProperty("exitStatus", stepExecution.getExitStatus());
        if (stepExecution instanceof ExtendedStepExecution) {
            final ExtendedStepExecution extendedStepExecution = (ExtendedStepExecution) stepExecution;
            properties.setProperty("jobExecutionId", Long.toString(extendedStepExecution.getJobExecutionId()));
        }
        for (final Metric metric : stepExecution.getMetrics()) {
            properties.setProperty("metric->" + metric.getType().toString(), Long.toString(metric.getValue()));
        }
    }
}
