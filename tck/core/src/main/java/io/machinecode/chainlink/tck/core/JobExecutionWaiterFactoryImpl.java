package io.machinecode.chainlink.tck.core;

import com.ibm.jbatch.tck.spi.JobExecutionWaiter;
import com.ibm.jbatch.tck.spi.JobExecutionWaiterFactory;
import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.spi.Constants;

import javax.batch.operations.JobOperator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobExecutionWaiterFactoryImpl implements JobExecutionWaiterFactory {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                ((JobOperatorImpl)Chainlink.environment().getJobOperator(Constants.DEFAULT_CONFIGURATION)).shutdown();
            }
        });
    }

    @Override
    public JobExecutionWaiter createWaiter(final long executionId, final JobOperator operator, final long timeout) {
        return new JobExecutionWaiterImpl(executionId, operator, timeout);
    }
}
