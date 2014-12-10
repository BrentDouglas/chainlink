package io.machinecode.chainlink.core.management.jmx;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JmxOperatorMBean {

    void startSession() throws Exception;

    Set<String> getJobNames() throws Exception;

    int getJobInstanceCount(final String jobName) throws Exception;

    List<Properties> getJobInstances(String jobName, int start, int count) throws Exception;

    List<Long> getRunningExecutions(String jobName) throws Exception;

    Properties getParameters(long jobExecutionId) throws Exception;

    Properties getJobInstance(long jobExecutionId) throws Exception;

    List<Properties> getJobExecutions(long jobInstanceId) throws Exception;

    Properties getJobExecution(long jobExecutionId) throws Exception;

    List<Properties> getStepExecutions(long jobExecutionId) throws Exception;
}
