package io.machinecode.chainlink.core.management.jmx;

import io.machinecode.chainlink.spi.management.ExtendedJobOperator;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JmxSession implements JmxSessionMBean {

    private final ExtendedJobOperator operator;
    private final MBeanServer server;
    private final ObjectName name;
    private final Properties properties = new Properties();

    public JmxSession(final ExtendedJobOperator operator, final MBeanServer server, final ObjectName name) {
        this.operator = operator;
        this.server = server;
        this.name = name;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public void setProperty(final String key, final String value) {
        properties.setProperty(key, value);
    }

    @Override
    public void removeProperty(final String key) {
        properties.remove(key);
    }

    @Override
    public void clearProperties() {
        properties.clear();
    }

    @Override
    public void endSession() throws Exception {
        server.unregisterMBean(name);
    }

    @Override
    public long start(final String jslName) {
        return operator.start(jslName, properties);
    }

    @Override
    public long restart(final long jobExecutionId) {
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
