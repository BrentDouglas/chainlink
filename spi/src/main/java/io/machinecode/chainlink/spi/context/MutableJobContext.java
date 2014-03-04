package io.machinecode.chainlink.spi.context;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface MutableJobContext extends JobContext {

    void setBatchStatus(BatchStatus batchStatus);

    void setFrom(final JobContext jobContext);
}
