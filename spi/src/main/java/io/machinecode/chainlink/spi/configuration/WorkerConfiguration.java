package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.execution.Executor;

import javax.management.MBeanServer;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface WorkerConfiguration extends ExecutorConfiguration {

    Executor getExecutor();

    MBeanServer getMBeanServer();
}
