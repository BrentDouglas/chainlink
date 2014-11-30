package io.machinecode.chainlink.spi.work;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface JobWork extends Job, Work, Serializable {

    Chain<?> before(final RuntimeConfiguration configuration, final ExecutionRepositoryId executionRepositoryId,
                       final WorkerId workerId, final ExecutableId callbackId, final ExecutionContext context) throws Exception;

    void after(final RuntimeConfiguration configuration, final ExecutionRepositoryId executionRepositoryId,
               final WorkerId workerId, final ExecutableId parentId, final ExecutionContext context) throws Exception;

    /**
     * @param next The id of the element to extract
     * @return The element in the job with id {@param next}
     */
    ExecutionWork getNextExecution(final String next);

    boolean isRestartable();
}
