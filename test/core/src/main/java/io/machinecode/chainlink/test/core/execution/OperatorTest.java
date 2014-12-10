package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.core.management.JobOperatorImpl;
import org.junit.Assert;
import org.junit.Before;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class OperatorTest extends BaseTest {

    protected JobOperatorImpl operator;

    @Before
    public void before() throws Exception {
        if (operator == null) {
            operator = new JobOperatorImpl(configuration(), new Properties());
        }
    }

    protected void assertFinishedWith(final BatchStatus status, final long jobExecutionId) throws Exception {
        final JobExecution execution = repository().getJobExecution(jobExecutionId);
        Assert.assertEquals("Batch Status", status, execution.getBatchStatus());
        Assert.assertEquals("Exit Status", status.name(), execution.getExitStatus());
    }
}
