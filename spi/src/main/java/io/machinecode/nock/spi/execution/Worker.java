package io.machinecode.nock.spi.execution;

import io.machinecode.nock.spi.context.ThreadId;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Worker extends Runnable {

    ThreadId getThreadId();

    void addExecutable(final ExecutableEvent event);
}
