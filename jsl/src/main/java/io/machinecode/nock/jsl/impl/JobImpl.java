package io.machinecode.nock.jsl.impl;

import io.machinecode.nock.jsl.api.Job;
import io.machinecode.nock.jsl.api.Listeners;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.impl.type.ExecutionImpl;
import io.machinecode.nock.jsl.validation.InvalidJobDefinitionException;
import io.machinecode.nock.jsl.validation.JobValidator;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobImpl implements Job {

    private final String id;
    private final String version;
    private final boolean restartable;
    private final Properties properties;
    private final Listeners listeners;
    private final List<? extends Execution> executions;

    public JobImpl(final Job that) throws InvalidJobDefinitionException {
        JobValidator.INSTANCE.validate(that);
        this.id = that.getId();
        this.version = that.getVersion();
        this.restartable = that.isRestartable();
        this.properties = new PropertiesImpl(that.getProperties());
        this.listeners = new ListenersImpl(that.getListeners());
        this.executions = ExecutionImpl.immutableCopyExecutions(that.getExecutions());
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
    public boolean isRestartable() {
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
