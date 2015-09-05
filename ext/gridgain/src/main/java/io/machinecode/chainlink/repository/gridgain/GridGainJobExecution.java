/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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
