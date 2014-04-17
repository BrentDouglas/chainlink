package io.machinecode.chainlink.core.management.jmx;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
