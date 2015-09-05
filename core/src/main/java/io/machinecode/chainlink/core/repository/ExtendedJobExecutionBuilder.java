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
package io.machinecode.chainlink.core.repository;

import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import javax.batch.runtime.BatchStatus;
import java.util.Date;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ExtendedJobExecutionBuilder<T extends ExtendedJobExecutionBuilder<T>> {

    T setJobExecutionId(final long jobExecutionId);

    T setJobName(final String jobName);

    T setBatchStatus(final BatchStatus batchStatus);

    T setStartTime(final Date startTime);

    T setEndTime(final Date endTime);

    T setExitStatus(final String exitStatus);

    T setCreateTime(final Date createTime);

    T setLastUpdatedTime(final Date lastUpdatedTime);

    T setJobParameters(final Properties jobParameters);

    T setRestartElementId(final String restartElementId);

    T setJobInstanceId(final long jobInstanceId);

    ExtendedJobExecution build();
}
