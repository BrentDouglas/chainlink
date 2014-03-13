package io.machinecode.chainlink.core.context;

import io.machinecode.chainlink.spi.context.MutableJobContext;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.context.JobContext;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobContextImpl implements MutableJobContext {

    private static final Logger log = Logger.getLogger(JobContextImpl.class);

    private final String jobName;
    private final long jobInstanceId;
    private final long jobExecutionId;
    private final Properties properties;
    private transient Object transientUserData;
    private BatchStatus batchStatus;
    private String exitStatus;

    public JobContextImpl(final JobContext context) {
        this.jobName = context.getJobName();
        this.jobInstanceId = context.getInstanceId();
        this.jobExecutionId = context.getExecutionId();
        this.properties = context.getProperties();
        this.batchStatus = BatchStatus.STARTED;
        this.exitStatus = null;
    }

    public JobContextImpl(final JobInstance instance, final JobExecution execution, final Properties properties) {
        this.jobName = instance.getJobName();
        this.jobInstanceId = instance.getInstanceId();
        this.jobExecutionId = execution.getExecutionId();
        this.properties = properties;
        this.batchStatus = BatchStatus.STARTING;
        this.exitStatus = null;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public Object getTransientUserData() {
        return transientUserData;
    }

    @Override
    public void setTransientUserData(final Object data) {
        this.transientUserData = data;
    }

    @Override
    public long getInstanceId() {
        return jobInstanceId;
    }

    @Override
    public long getExecutionId() {
        return jobExecutionId;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    @Override
    public void setBatchStatus(final BatchStatus batchStatus) {
        log.debugf(Messages.get("CHAINLINK-029000.job.context.batch.status"), jobExecutionId, jobName, batchStatus);
        this.batchStatus = batchStatus;
    }

    @Override
    public String getExitStatus() {
        return exitStatus;
    }

    @Override
    public void setExitStatus(final String exitStatus) {
        log.debugf(Messages.get("CHAINLINK-029001.job.context.exit.status"), jobExecutionId, jobName, exitStatus);
        this.exitStatus = exitStatus;
    }
}
