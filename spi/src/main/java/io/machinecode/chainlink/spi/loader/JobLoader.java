package io.machinecode.chainlink.spi.loader;

import io.machinecode.chainlink.spi.jsl.Job;

import javax.batch.operations.NoSuchJobException;

/**
 * <p>Provides {@link Job}'s to the Chainlink runtime.</p>
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JobLoader {

    /**
     * <p>Load a job from the named file (or equivalent).</p>
     *
     * @param jslName The name of the xml file (or equivalent) containing the job.
     * @return The {@link Job} contained within the reference to {@param jslName}.
     * @throws NoSuchJobException If this loader does not contain the specified job.
     */
    Job load(final String jslName) throws NoSuchJobException;
}
