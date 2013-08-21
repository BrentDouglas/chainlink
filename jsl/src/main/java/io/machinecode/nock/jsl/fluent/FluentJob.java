package io.machinecode.nock.jsl.fluent;

import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.Listeners;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.execution.Execution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentJob implements Job {

    private String id;
    private String version = "1.0";
    private String restartable = "true";
    private final FluentProperties properties = new FluentProperties();
    private final FluentListeners listeners = new FluentListeners();
    private final List<Execution> executions = new ArrayList<Execution>(0);

    @Override
    public String getId() {
        return this.id;
    }

    public FluentJob setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    public FluentJob setVersion(final String version) {
        this.version = version;
        return this;
    }

    @Override
    public String isRestartable() {
        return this.restartable;
    }

    public FluentJob setRestartable(final String restartable) {
        this.restartable = restartable;
        return this;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    public FluentJob addProperty(final String name, final String value) {
        this.properties.addProperty(name, value);
        return this;
    }

    @Override
    public Listeners getListeners() {
        return this.listeners;
    }

    public FluentJob addListener(final Listener listener) {
        this.listeners.addListener(listener);
        return this;
    }

    @Override
    public List<Execution> getExecutions() {
        return this.executions;
    }

    public FluentJob addExecution(final Execution execution) {
        this.executions.add(execution);
        return this;
    }
}
