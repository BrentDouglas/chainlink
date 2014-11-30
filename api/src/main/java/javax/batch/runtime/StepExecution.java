package javax.batch.runtime;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
