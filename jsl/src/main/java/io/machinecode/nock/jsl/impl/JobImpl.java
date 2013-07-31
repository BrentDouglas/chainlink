package io.machinecode.nock.jsl.impl;

import io.machinecode.nock.jsl.api.Job;
import io.machinecode.nock.jsl.api.Listeners;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.type.Type;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobImpl implements Job {

    private final String id;
    private final String version;
    private final Boolean restartable;
    private final Properties properties;
    private final Listeners listeners;
    private final List<Type> types;

    public JobImpl(final Job job) {
        this.id = job.getId();
        this.version = job.getVersion();
        this.restartable = job.isRestartable();
        this.properties = new PropertiesImpl(job.getProperties());
        this.listeners = new ListenersImpl(job.getListeners());
        this.types = Util.immutableCopy(job.getTypes()); //TODO
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
    public Boolean isRestartable() {
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
    public List<Type> getTypes() {
        return this.types;
    }
}
