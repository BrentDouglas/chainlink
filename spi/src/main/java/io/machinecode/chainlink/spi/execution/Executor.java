package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.Lifecycle;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Executor extends Lifecycle {

    Worker getWorker() throws Exception;

    Worker getWorker(final WorkerId id) throws Exception;

    List<Worker> getWorkers(final int required) throws Exception;
}
