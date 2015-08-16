/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.repository.gridgain;

import io.machinecode.chainlink.core.repository.JobInstanceImpl;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import org.gridgain.grid.cache.query.GridCacheQuerySqlField;

import java.util.Date;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainJobInstance extends JobInstanceImpl {
    private static final long serialVersionUID = 1L;

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
