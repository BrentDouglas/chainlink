package io.machinecode.chainlink.spi.repository;

import javax.batch.runtime.JobInstance;
import java.util.Date;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ExtendedJobInstance extends JobInstance {

    /**
     * @return The jsl name of the job associated with this instance.
     */
    String getJslName();

    Date getCreateTime();
}
