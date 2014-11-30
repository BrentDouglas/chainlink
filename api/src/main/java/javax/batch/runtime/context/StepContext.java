package javax.batch.runtime.context;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import java.io.Serializable;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface StepContext {

    String getStepName();

    Object getTransientUserData();

    void setTransientUserData(Object data);

    long getStepExecutionId();

    Properties getProperties();

    Serializable getPersistentUserData();

    void setPersistentUserData(Serializable data);

    BatchStatus getBatchStatus();

    String getExitStatus();

    void setExitStatus(String exitStatus);

    Exception getException();

    Metric[] getMetrics();
}
