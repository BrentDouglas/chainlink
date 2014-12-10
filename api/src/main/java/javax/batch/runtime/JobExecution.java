package javax.batch.runtime;

import java.util.Date;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JobExecution {

    long getExecutionId();

    String getJobName();

    BatchStatus getBatchStatus();

    Date getStartTime();

    Date getEndTime();

    String getExitStatus();

    Date getCreateTime();

    Date getLastUpdatedTime();

    Properties getJobParameters();
}
