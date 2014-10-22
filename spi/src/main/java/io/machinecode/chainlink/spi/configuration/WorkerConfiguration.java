package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.execution.Executor;

import javax.management.MBeanServer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface WorkerConfiguration extends ExecutorConfiguration {

    Executor getExecutor();

    MBeanServer getMBeanServer();
}
