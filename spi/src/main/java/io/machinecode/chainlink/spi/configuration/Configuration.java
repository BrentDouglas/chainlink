package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;
import io.machinecode.chainlink.spi.execution.Executor;

import javax.management.MBeanServer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Configuration extends ExecutorConfiguration {

    Executor getExecutor();

    WorkerFactory getWorkerFactory();

    MBeanServer getMBeanServer();

    RuntimeConfiguration getRuntimeConfiguration();
}
