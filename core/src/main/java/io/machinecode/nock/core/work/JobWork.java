package io.machinecode.nock.core.work;

import io.machinecode.nock.core.work.execution.ExecutionWork;
import io.machinecode.nock.spi.element.Job;

import javax.batch.api.listener.JobListener;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobWork implements Work, JobExecution, Job {

    private final String id;
    private final String version;
    private final String restartable;
    private final ListenersWork<JobListener> listeners;
    private final List<ExecutionWork> executions;


    public JobWork(final String id, final String version, final String restartable,
                   final ListenersWork<JobListener> listeners, final List<ExecutionWork> executions) {
        this.id = id;
        this.version = version;
        this.restartable = restartable;
        this.listeners = listeners;
        this.executions = executions;
    }

    @Override
    public long getExecutionId() {
        return Long.parseLong(id);
    }

    @Override
    public String getJobName() {
        return id;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getStartTime() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getEndTime() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getExitStatus() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getCreateTime() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getLastUpdatedTime() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Properties getJobParameters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //Job

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String isRestartable() {
        return this.restartable;
    }

    @Override
    public io.machinecode.nock.spi.element.Properties getProperties() {
        return null;
    }

    @Override
    public ListenersWork<JobListener> getListeners() {
        return this.listeners;
    }

    @Override
    public List<ExecutionWork> getExecutions() {
        return this.executions;
    }
}
