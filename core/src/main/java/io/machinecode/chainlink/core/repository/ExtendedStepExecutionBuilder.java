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
package io.machinecode.chainlink.core.repository;

import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ExtendedStepExecutionBuilder<T extends ExtendedStepExecutionBuilder<T>> extends BaseExecutionBuilder<T> {

    T setStepExecutionId(final long stepExecutionId);

    T setStepName(final String stepName);

    T setBatchStatus(final BatchStatus batchStatus);

    T setStartTime(final Date startTime);

    T setEndTime(final Date endTime);

    T setExitStatus(final String exitStatus);

    T setPersistentUserData(final Serializable persistentUserData);

    T setMetrics(final Metric[] metrics);

    T setJobExecutionId(final long jobExecutionId);

    ExtendedStepExecution build();
}
