package io.machinecode.chainlink.core.management.jmx;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface JmxSessionMBean {

    void endSession() throws Exception;

    Properties getProperties() throws Exception;

    void setProperty(final String key, final String value) throws Exception;

    void removeProperty(final String key) throws Exception;

    void clearProperties() throws Exception;

    long start(final String jslName) throws Exception;

    long restart(final long jobExecutionId) throws Exception;

    void stop(final long jobExecutionId) throws Exception;

    void abandon(final long jobExecutionId) throws Exception;
}
