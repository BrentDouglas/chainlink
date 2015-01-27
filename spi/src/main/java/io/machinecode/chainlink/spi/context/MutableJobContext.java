package io.machinecode.chainlink.spi.context;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MutableJobContext extends JobContext {

    void setBatchStatus(final BatchStatus batchStatus);
}
