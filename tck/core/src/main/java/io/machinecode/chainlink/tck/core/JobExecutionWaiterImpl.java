package io.machinecode.chainlink.tck.core;

import com.ibm.jbatch.tck.spi.JobExecutionTimeoutException;
import com.ibm.jbatch.tck.spi.JobExecutionWaiter;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.management.JobOperation;

import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.JobExecution;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
        } catch (final TimeoutException e) {
            throw new JobExecutionTimeoutException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
