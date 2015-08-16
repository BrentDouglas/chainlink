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
package io.machinecode.chainlink.core.base;

import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.spi.management.JobOperation;
import org.junit.Before;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.StepExecution;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class OperatorTest extends BaseTest {

    protected JobOperatorImpl operator;

    @Before
    public void before() throws Exception {
        if (operator == null) {
            operator = new JobOperatorImpl(configuration());
            operator.open(configuration());
        }
    }

    protected void assertJobFinishedWith(final JobOperation operation, final BatchStatus status) throws Exception {
        assertJobFinishedWith(operation, status, status.name());
    }

    protected void assertJobFinishedWith(final JobOperation operation, final BatchStatus status, final String exitStatus) throws Exception {
        final JobExecution execution = repository().getJobExecution(operation.getJobExecutionId());
        assertEquals("Batch Status", status, execution.getBatchStatus());
        assertEquals("Exit Status", exitStatus, execution.getExitStatus());
    }

    protected void assertStepFinishedWith(final JobOperation operation, final BatchStatus status) throws Exception {
        assertStepFinishedWith(operation, status, status.name());
    }

    protected void assertStepFinishedWith(final JobOperation operation, final BatchStatus status, final String exitStatus) throws Exception {
        final StepExecution execution = repository().getStepExecutionsForJobExecution(operation.getJobExecutionId()).get(0);
        assertEquals("Batch Status", status, execution.getBatchStatus());
        assertEquals("Exit Status", exitStatus, execution.getExitStatus());
    }
}
