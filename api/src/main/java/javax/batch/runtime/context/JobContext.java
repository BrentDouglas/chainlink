package javax.batch.runtime.context;

import javax.batch.runtime.BatchStatus;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
