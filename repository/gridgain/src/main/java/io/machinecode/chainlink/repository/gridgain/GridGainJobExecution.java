package io.machinecode.chainlink.repository.gridgain;

import io.machinecode.chainlink.core.repository.JobExecutionImpl;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import org.gridgain.grid.cache.query.GridCacheQuerySqlField;

import javax.batch.runtime.BatchStatus;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainJobExecution extends JobExecutionImpl {
    private static final long serialVersionUID = 1L;

    public GridGainJobExecution(final _Builder builder) {
        super(builder);
    }

    public GridGainJobExecution(final ExtendedJobExecution builder) {
        super(builder);
    }

    @GridCacheQuerySqlField(name = "jobExecutionId")
    @Override
    public long getExecutionId() {
        return super.getExecutionId();
    }

    @GridCacheQuerySqlField(index = true)
    @Override
    public long getJobInstanceId() {
        return super.getJobInstanceId();
    }

    @GridCacheQuerySqlField(index = true)
    @Override
    public String getJobName() {
        return super.getJobName();
    }

    @GridCacheQuerySqlField
    @Override
    public BatchStatus getBatchStatus() {
        return super.getBatchStatus();
    }

    public static class Builder extends _Builder<Builder> {
        public GridGainJobExecution build() {
            return new GridGainJobExecution(this);
        }

    }
}
