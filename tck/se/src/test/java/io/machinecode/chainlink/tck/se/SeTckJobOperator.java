package io.machinecode.chainlink.tck.se;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.core.management.JobOperatorView;
import io.machinecode.chainlink.se.management.SeEnvironment;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.management.JobOperation;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import javax.batch.operations.JobExecutionAlreadyCompleteException;
import javax.batch.operations.JobExecutionIsRunningException;
import javax.batch.operations.JobExecutionNotMostRecentException;
import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.JobRestartException;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.JobStartException;
import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class SeTckJobOperator extends JobOperatorView {

    static {
        Chainlink.setEnvironment(new SeEnvironment());
    }

    public SeTckJobOperator() {
        super(Constants.DEFAULT_CONFIGURATION);
    }

    public SeTckJobOperator(final String id) {
        super(Chainlink.getEnvironment().getJobOperator(id));
    }

    public SeTckJobOperator(final ExtendedJobOperator delegate) {
        super(delegate);
    }
}
