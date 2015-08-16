/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.management.jmx;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JmxJobOperatorBean implements JmxJobOperatorBeanMBean, AutoCloseable {

    protected final ExtendedJobOperator operator;
    protected final Configuration configuration;
    protected final ObjectName name;

    public JmxJobOperatorBean(final ExtendedJobOperator operator, final Configuration configuration, final String... extra) throws NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, MalformedObjectNameException {
        this.operator = operator;
        this.configuration = configuration;
        final String domain = ObjectName.quote(configuration.getProperty(Constants.JMX_DOMAIN, Constants.Defaults.JMX_DOMAIN));
        final MBeanServer server = configuration.getMBeanServer();
        if (server != null) {
            name = new ObjectName(_objectName(domain, extra));
            if (!server.isRegistered(name)) {
                server.registerMBean(this, name);
            }
        } else {
            name = null;
        }
    }

    public ObjectName getName() {
        return name;
    }

    private String _objectName(final String domain, final String... extras) {
        final StringBuilder builder = new StringBuilder();
        builder.append(domain)
                .append(":type=JobOperator,name=JobOperator");
        for (final String extra : extras) {
            builder.append(extra);
        }
        return builder.toString();
    }

    @Override
    public void close() throws Exception {
        final MBeanServer server = configuration.getMBeanServer();
        if (server != null) {
            if (server.isRegistered(name)) {
                server.unregisterMBean(name);
            }
        }
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
    public TabularData getJobInstances(final String jobName, final int start, final int count) throws OpenDataException {
        final List<JobInstance> jobInstances = operator.getJobInstances(jobName, start, count);
        final TabularData data = new TabularDataSupport(JmxUtils.LIST_JOB_INSTANCE);
        for (final JobInstance jobInstance : jobInstances) {
            data.put(JmxUtils.writeJobInstance(jobInstance));
        }
        return data;
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) {
        return operator.getRunningExecutions(jobName);
    }

    @Override
    public TabularData getParameters(final long jobExecutionId) throws OpenDataException {
        return JmxUtils.writeProperties(operator.getJobExecution(jobExecutionId).getJobParameters());
    }

    @Override
    public CompositeData getJobInstance(final long jobExecutionId) throws OpenDataException {
        final ExtendedJobInstance jobInstance = operator.getJobInstance(jobExecutionId);
        return JmxUtils.writeExtendedJobInstance(jobInstance);
    }

    @Override
    public CompositeData getJobInstanceById(final long jobInstanceId) throws Exception {
        final ExtendedJobInstance jobInstance = operator.getJobInstanceById(jobInstanceId);
        return JmxUtils.writeExtendedJobInstance(jobInstance);
    }

    @Override
    public TabularData getJobExecutions(final long jobInstanceId) throws OpenDataException {
        final JobInstance jobInstance = operator.getJobInstanceById(jobInstanceId);
        final List<JobExecution> jobExecutions = operator.getJobExecutions(jobInstance);
        final TabularData data = new TabularDataSupport(JmxUtils.LIST_JOB_EXECUTION);
        for (final JobExecution jobExecution : jobExecutions) {
            data.put(JmxUtils.writeJobExecution(jobExecution));
        }
        return data;
    }

    @Override
    public CompositeData getJobExecution(final long jobExecutionId) throws OpenDataException {
        final ExtendedJobExecution jobExecution = operator.getJobExecution(jobExecutionId);
        return JmxUtils.writeExtendedJobExecution(jobExecution);
    }

    @Override
    public TabularData getStepExecutions(final long jobExecutionId) throws Exception {
        final List<StepExecution> stepExecutions = operator.getStepExecutions(jobExecutionId);
        final TabularData data = new TabularDataSupport(JmxUtils.LIST_STEP_EXECUTION);
        for (final StepExecution stepExecution : stepExecutions) {
            data.put(JmxUtils.writeStepExecution(stepExecution, configuration.getMarshalling()));
        }
        return data;
    }

    @Override
    public long start(final String jslName, final String parameters) throws IOException {
        final Properties properties = new Properties();
        properties.load(new StringReader(parameters));
        return operator.start(jslName, properties);
    }

    @Override
    public long restart(final long jobExecutionId, final String parameters) throws IOException {
        final Properties properties = new Properties();
        properties.load(new StringReader(parameters));
        return operator.restart(jobExecutionId, properties);
    }

    @Override
    public void stop(final long jobExecutionId) {
        operator.stop(jobExecutionId);
    }

    @Override
    public void abandon(final long jobExecutionId) {
        operator.abandon(jobExecutionId);
    }
}
