package javax.batch.runtime.context;

import javax.batch.runtime.BatchStatus;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JobContext {

    String getJobName();

    Object getTransientUserData();

    void setTransientUserData(Object data);

    long getInstanceId();

    long getExecutionId();

    Properties getProperties();

    BatchStatus getBatchStatus();

    String getExitStatus();

    void setExitStatus(String exitStatus);
}
