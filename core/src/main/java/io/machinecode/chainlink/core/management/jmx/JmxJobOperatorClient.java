/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JmxJobOperatorClient {

    protected final MBeanServer server;
    protected final ObjectName name;

    final String intFqcn;
    final String longFqcn;
    final String stringFqcn;

    public JmxJobOperatorClient(final MBeanServer server, final ObjectName name) {
        this.server = server;
        this.name = name;

        this.intFqcn = int.class.getName();
        this.longFqcn = long.class.getName();
        this.stringFqcn = String.class.getName();
    }

    @SuppressWarnings("unchecked")
    public Set<String> getJobNames() throws AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        return (Set<String>)server.getAttribute(name, "JobNames");
    }

    public int getJobInstanceCount(final String jobName) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (int)server.invoke(name,
                "getJobInstanceCount",
                new Object[]{jobName},
                new String[]{stringFqcn }
        );
    }

    public TabularData getJobInstances(final String jobName, final int start, final int count)  throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (TabularData)server.invoke(name,
                "getJobInstances",
                new Object[]{jobName, start, count},
                new String[]{stringFqcn, intFqcn, intFqcn}
        );
    }

    @SuppressWarnings("unchecked")
    public List<Long> getRunningExecutions(final String jobName) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (List<Long>)server.invoke(name,
                "getRunningExecutions",
                new Object[]{jobName},
                new String[]{stringFqcn}
        );
    }

    public TabularData getParameters(final long jobExecutionId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (TabularData)server.invoke(name,
                "getParameters",
                new Object[]{jobExecutionId},
                new String[]{longFqcn}
        );
    }

    public CompositeData getJobInstance(final long jobExecutionId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (CompositeData)server.invoke(name,
                "getJobInstance",
                new Object[]{jobExecutionId},
                new String[]{longFqcn}
        );
    }

    public CompositeData getJobInstanceById(final long jobInstanceId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (CompositeData)server.invoke(name,
                "getJobInstanceById",
                new Object[]{jobInstanceId},
                new String[]{longFqcn}
        );
    }

    public TabularData getJobExecutions(final long jobInstanceId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (TabularData)server.invoke(name,
                "getJobExecutions",
                new Object[]{jobInstanceId},
                new String[]{longFqcn}
        );
    }

    public CompositeData getJobExecution(final long jobExecutionId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (CompositeData)server.invoke(name,
                "getJobExecution",
                new Object[]{jobExecutionId},
                new String[]{longFqcn}
        );
    }

    public TabularData getStepExecutions(final long jobExecutionId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (TabularData)server.invoke(name,
                "getStepExecutions",
                new Object[]{jobExecutionId},
                new String[]{longFqcn}
        );
    }

    public long start(final String jslName, final String parameters) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (long)server.invoke(name,
                "start",
                new Object[]{jslName, parameters},
                new String[]{stringFqcn, stringFqcn}
        );
    }

    public long restart(final long jobExecutionId, final String parameters) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (long)server.invoke(name,
                "restart",
                new Object[]{jobExecutionId, parameters},
                new String[]{longFqcn, stringFqcn}
        );
    }

    public void stop(final long jobExecutionId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        server.invoke(name,
                "stop",
                new Object[]{jobExecutionId},
                new String[]{longFqcn}
        );
    }

    public void abandon(final long jobExecutionId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        server.invoke(name,
                "abandon",
                new Object[]{jobExecutionId},
                new String[]{longFqcn}
        );
    }
}
