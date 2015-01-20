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
public class JmxJobOperatorClient implements JmxJobOperatorBeanMBean {

    protected final MBeanServer server;
    protected final ObjectName name;

    final String intc;
    final String longc;
    final String stringc;

    public JmxJobOperatorClient(final MBeanServer server, final ObjectName name) {
        this.server = server;
        this.name = name;

        this.intc = int.class.getName();
        this.longc = long.class.getName();
        this.stringc = String.class.getName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getJobNames() throws AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        return (Set<String>)server.getAttribute(name, "JobNames");
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (int)server.invoke(name,
                "getJobInstanceCount",
                new Object[]{ jobName },
                new String[]{ stringc }
        );
    }

    @Override
    public TabularData getJobInstances(final String jobName, final int start, final int count)  throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (TabularData)server.invoke(name,
                "getJobInstances",
                new Object[]{ jobName, start, count },
                new String[]{ stringc, intc, intc }
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Long> getRunningExecutions(final String jobName) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (List<Long>)server.invoke(name,
                "getRunningExecutions",
                new Object[]{ jobName },
                new String[]{ stringc }
        );
    }

    @Override
    public TabularData getParameters(final long jobExecutionId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (TabularData)server.invoke(name,
                "getParameters",
                new Object[]{ jobExecutionId },
                new String[]{ longc }
        );
    }

    @Override
    public CompositeData getJobInstance(final long jobExecutionId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (CompositeData)server.invoke(name,
                "getJobInstance",
                new Object[]{ jobExecutionId },
                new String[]{ longc }
        );
    }

    @Override
    public CompositeData getJobInstanceById(final long jobInstanceId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (CompositeData)server.invoke(name,
                "getJobInstanceById",
                new Object[]{ jobInstanceId },
                new String[]{ longc }
        );
    }

    @Override
    public TabularData getJobExecutions(final long jobInstanceId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (TabularData)server.invoke(name,
                "getJobExecutions",
                new Object[]{ jobInstanceId },
                new String[]{ longc }
        );
    }

    @Override
    public CompositeData getJobExecution(final long jobExecutionId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (CompositeData)server.invoke(name,
                "getJobExecution",
                new Object[]{ jobExecutionId },
                new String[]{ longc }
        );
    }

    @Override
    public TabularData getStepExecutions(final long jobExecutionId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (TabularData)server.invoke(name,
                "getStepExecutions",
                new Object[]{ jobExecutionId },
                new String[]{ longc }
        );
    }

    @Override
    public long start(final String jslName, final String parameters) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (long)server.invoke(name,
                "start",
                new Object[]{ jslName, parameters },
                new String[]{ stringc, stringc }
        );
    }

    @Override
    public long restart(final long jobExecutionId, final String parameters) throws MBeanException, InstanceNotFoundException, ReflectionException {
        return (long)server.invoke(name,
                "restart",
                new Object[]{ jobExecutionId, parameters },
                new String[]{ longc, stringc }
        );
    }

    @Override
    public void stop(final long jobExecutionId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        server.invoke(name,
                "stop",
                new Object[]{ jobExecutionId },
                new String[]{ longc }
        );
    }

    @Override
    public void abandon(final long jobExecutionId) throws MBeanException, InstanceNotFoundException, ReflectionException {
        server.invoke(name,
                "abandon",
                new Object[]{ jobExecutionId },
                new String[]{ longc }
        );
    }
}
