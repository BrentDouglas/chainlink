package io.machinecode.nock.spi.loader;

import io.machinecode.nock.spi.element.Job;

import javax.batch.operations.NoSuchJobException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
