package io.machinecode.nock.core.model;

import io.machinecode.nock.core.model.execution.ExecutionImpl;
import io.machinecode.nock.jsl.validation.InvalidJobException;
import io.machinecode.nock.spi.element.Job;

import javax.batch.api.listener.JobListener;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobImpl implements Job {

    private final String id;
    private final String version;
    private final String restartable;
    private final PropertiesImpl properties;
    private final ListenersImpl<JobListener> listeners;
    private final List<ExecutionImpl> executions;

    public JobImpl(final String id, final String version, final String restartable, final PropertiesImpl properties,
                   final ListenersImpl<JobListener> listeners, final List<ExecutionImpl> executions) throws InvalidJobException {
        this.id = id;
        this.version = version;
        this.restartable = restartable;
        this.properties = properties;
        this.listeners = listeners;
        this.executions = executions;
    }

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
    public PropertiesImpl getProperties() {
        return this.properties;
    }

    @Override
    public ListenersImpl<JobListener> getListeners() {
        return this.listeners;
    }

    @Override
    public List<ExecutionImpl> getExecutions() {
        return this.executions;
    }
}
