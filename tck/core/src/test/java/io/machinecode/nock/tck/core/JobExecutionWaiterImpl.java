package io.machinecode.nock.tck.core;

import com.ibm.jbatch.tck.spi.JobExecutionTimeoutException;
import com.ibm.jbatch.tck.spi.JobExecutionWaiter;
import io.machinecode.nock.core.work.RepositoryStatus;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.JobExecution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobExecutionWaiterImpl implements JobExecutionWaiter {
    private final long executionId;
    private final JobOperator operator;
    private final long timeout;

    public JobExecutionWaiterImpl(final long executionId, final JobOperator operator, final long timeout) {
        this.executionId = executionId;
        this.operator = operator;
        this.timeout = timeout;
    }

    @Override
    public JobExecution awaitTermination() throws JobExecutionTimeoutException {
        final long end = System.currentTimeMillis() + timeout;
        do {
            final JobExecution execution = operator.getJobExecution(executionId);
            if (RepositoryStatus.isComplete(execution.getBatchStatus())) {
                return execution;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new JobExecutionTimeoutException();
            }
        } while (System.currentTimeMillis() < end);
        throw new JobExecutionTimeoutException();
    }
}
