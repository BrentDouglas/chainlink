package io.machinecode.chainlink.repository.gridgain;

import io.machinecode.chainlink.repository.core.JobInstanceImpl;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import org.gridgain.grid.cache.query.GridCacheQuerySqlField;

import java.util.Date;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class GridGainJobInstance extends JobInstanceImpl {

    public GridGainJobInstance(final _Builder builder) {
        super(builder);
    }

    public GridGainJobInstance(final ExtendedJobInstance builder) {
        super(builder);
    }

    @GridCacheQuerySqlField(index = true)
    @Override
    public long getInstanceId() {
        return super.getInstanceId();
    }

    @GridCacheQuerySqlField(index = true)
    @Override
    public String getJobName() {
        return super.getJobName();
    }

    @GridCacheQuerySqlField
    @Override
    public String getJslName() {
        return super.getJslName();
    }

    @GridCacheQuerySqlField
    @Override
    public Date getCreateTime() {
        return super.getCreateTime();
    }

    public static class Builder extends _Builder<Builder> {
        public GridGainJobInstance build() {
            return new GridGainJobInstance(this);
        }
    }
}
