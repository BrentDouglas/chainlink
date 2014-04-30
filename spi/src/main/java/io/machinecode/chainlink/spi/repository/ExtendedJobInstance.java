package io.machinecode.chainlink.spi.repository;

import javax.batch.runtime.JobInstance;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExtendedJobInstance extends JobInstance, Serializable {

    /**
     * @return The jsl name of the job associated with this instance.
     */
    String getJslName();

    Date getCreateTime();
}
