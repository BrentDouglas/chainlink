package io.machinecode.chainlink.spi;

import javax.batch.runtime.JobInstance;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExtendedJobInstance extends JobInstance {

    /**
     * @return The jsl name of the job associated with this instance.
     */
    String getJslName();
}
