package io.machinecode.chainlink.spi.loader;

import io.machinecode.chainlink.spi.element.Job;

import javax.batch.operations.NoSuchJobException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JobLoader {

    /**
     *
     * @param jslName
     * @return The {@link Job} contained within the reference to {@param jslName}.
     * @throws NoSuchJobException If this loader does not contain the specified job.
     */
    Job load(final String jslName) throws NoSuchJobException;
}
