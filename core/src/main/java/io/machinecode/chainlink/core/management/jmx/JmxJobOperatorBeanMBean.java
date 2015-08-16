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

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JmxJobOperatorBeanMBean {

    Set<String> getJobNames() throws Exception;

    int getJobInstanceCount(final String jobName) throws Exception;

    TabularData getJobInstances(String jobName, int start, int count) throws Exception;

    List<Long> getRunningExecutions(String jobName) throws Exception;

    TabularData getParameters(long jobExecutionId) throws Exception;

    CompositeData getJobInstance(long jobExecutionId) throws Exception;

    CompositeData getJobInstanceById(long jobInstanceId) throws Exception;

    TabularData getJobExecutions(long jobInstanceId) throws Exception;

    CompositeData getJobExecution(long jobExecutionId) throws Exception;

    TabularData getStepExecutions(long jobExecutionId) throws Exception;

    long start(final String jslName, final String parameters) throws Exception;

    long restart(final long jobExecutionId, final String parameters) throws Exception;

    void stop(final long jobExecutionId) throws Exception;

    void abandon(final long jobExecutionId) throws Exception;
}
