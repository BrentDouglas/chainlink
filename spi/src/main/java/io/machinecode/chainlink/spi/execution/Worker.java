package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.context.ThreadId;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Worker extends Runnable {

    ThreadId getThreadId();

    void addExecutable(final ExecutableEvent event);

    void start();
}
