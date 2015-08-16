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
package io.machinecode.chainlink.tck.core;

import com.ibm.jbatch.tck.spi.JobExecutionTimeoutException;
import com.ibm.jbatch.tck.spi.JobExecutionWaiter;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.management.JobOperation;

import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.JobExecution;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobExecutionWaiterImpl implements JobExecutionWaiter {
    private final long executionId;
    private final ExtendedJobOperator operator;
    private final long timeout;

    public JobExecutionWaiterImpl(final long executionId, final JobOperator operator, final long timeout) {
        this.executionId = executionId;
        this.operator = (ExtendedJobOperator) operator;
        this.timeout = timeout;
    }

    @Override
    public JobExecution awaitTermination() throws JobExecutionTimeoutException {
        try {
            final JobOperation operation = operator.getJobOperation(executionId);
            return operation.get(timeout, TimeUnit.MILLISECONDS);
        } catch (final JobExecutionNotRunningException e) {
            return operator.getJobExecution(executionId);
        } catch (final CancellationException e) {
            return operator.getJobExecution(executionId);
        } catch (final ExecutionException e) {
            return operator.getJobExecution(executionId);
        } catch (final TimeoutException e) {
            throw new JobExecutionTimeoutException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
