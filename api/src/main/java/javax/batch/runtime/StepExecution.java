package javax.batch.runtime;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface StepExecution {

    long getStepExecutionId();

    String getStepName();

    BatchStatus getBatchStatus();

    Date getStartTime();

    Date getEndTime();

    String getExitStatus();

    Serializable getPersistentUserData();

    Metric[] getMetrics();
}
