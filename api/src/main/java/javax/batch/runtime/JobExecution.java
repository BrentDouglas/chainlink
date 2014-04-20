package javax.batch.runtime;

import java.util.Date;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
