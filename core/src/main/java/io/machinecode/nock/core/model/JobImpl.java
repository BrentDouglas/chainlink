package io.machinecode.nock.core.model;

import io.machinecode.nock.jsl.api.Job;
import io.machinecode.nock.jsl.api.Listeners;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.validation.InvalidJobException;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobImpl implements Job {

    private final String id;
    private final String version;
    private final String restartable;
    private final Properties properties;
    private final Listeners listeners;
    private final List<? extends Execution> executions;

    public JobImpl(final String id, final String version, final String restartable, final Properties properties,
                   final Listeners listeners, final List<? extends Execution> executions) throws InvalidJobException {
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
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public Listeners getListeners() {
        return this.listeners;
    }

    @Override
    public List<? extends Execution> getExecutions() {
        return this.executions;
    }
}
