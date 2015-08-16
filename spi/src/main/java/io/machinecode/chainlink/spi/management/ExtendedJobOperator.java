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
package io.machinecode.chainlink.spi.management;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import javax.batch.operations.JobExecutionAlreadyCompleteException;
import javax.batch.operations.JobExecutionNotMostRecentException;
import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.JobOperator;
import javax.batch.operations.JobRestartException;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.JobStartException;
import javax.batch.operations.NoSuchJobExecutionException;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ExtendedJobOperator extends JobOperator, Lifecycle {

    ExtendedJobInstance getJobInstance(long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException;

    ExtendedJobExecution getJobExecution(long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException;

    ExtendedJobInstance getJobInstanceById(final long jobInstanceId);

    JobOperation startJob(final String jslName, final Properties parameters) throws JobStartException, JobSecurityException;

    JobOperation getJobOperation(final long jobExecutionId) throws JobExecutionNotRunningException;

    JobOperation restartJob(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException;

    Future<?> stopJob(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException;
}
