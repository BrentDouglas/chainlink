package io.machinecode.nock.core.model;

import io.machinecode.nock.core.impl.JobContextImpl;
import io.machinecode.nock.core.model.execution.ExecutionImpl;
import io.machinecode.nock.core.util.PropertiesConverter;
import io.machinecode.nock.jsl.validation.InvalidJobException;
import io.machinecode.nock.jsl.validation.JobValidator;
import io.machinecode.nock.jsl.visitor.JobTraversal;
import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.JobWork;
import io.machinecode.nock.spi.work.Worker;

import javax.batch.api.listener.JobListener;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobImpl implements Job, JobWork {

    private final String id;
    private final String version;
    private final String restartable;
    private final PropertiesImpl properties;
    private final ListenersImpl listeners;
    private final List<ExecutionImpl> executions;
    private final JobTraversal traversal;

    private transient List<JobListener> _listeners;

    public JobImpl(final String id, final String version, final String restartable, final PropertiesImpl properties,
                   final ListenersImpl listeners, final List<ExecutionImpl> executions) throws InvalidJobException {
        this.id = id;
        this.version = version;
        this.restartable = restartable;
        this.properties = properties;
        this.listeners = listeners;
        this.executions = executions;
        this.traversal = new JobTraversal(JobValidator.INSTANCE.visit(this));
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
    public String getRestartable() {
        return this.restartable;
    }

    public boolean isRestartable() {
        return Boolean.parseBoolean(this.restartable);
    }

    @Override
    public PropertiesImpl getProperties() {
        return this.properties;
    }

    @Override
    public ListenersImpl getListeners() {
        return this.listeners;
    }

    @Override
    public List<ExecutionImpl> getExecutions() {
        return this.executions;
    }

    // Lifecycle

    @Override
    public void before(final Transport transport, final Context context) throws Exception {
        final Repository repository = transport.getRepository();
        final JobContextImpl jobContext = new JobContextImpl(
                repository.getJobInstance(context.getJobInstanceId()),
                repository.getJobExecution(context.getJobExecutionId()),
                PropertiesConverter.convert(this.properties)
        );
        context.setJobContext(jobContext);
        final InjectionContext injectionContext = transport.createInjectionContext(context);
        this._listeners = this.listeners.getListenersImplementing(injectionContext, JobListener.class);
        Exception exception = null;
        for (final JobListener listener : this._listeners) {
            try {
                listener.beforeJob();
            } catch (final Exception e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public Future<Void> runJob(final Worker worker, final Transport transport, final Context context) throws Exception {
        return worker.runExecution(this.executions.get(0), transport, context);
    }

    @Override
    public void after(final Transport transport, final Context context) throws Exception {
        try{
            Exception exception = null;
            for (final JobListener listener : this._listeners) { //TODO Check these aren't null? Should never leave this node though
                try {
                    listener.afterJob();
                } catch (final Exception e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
        } finally {
            context.setJobContext(null);
        }
    }

    @Override
    public ExecutionWork next(final String next) {
        return traversal.next(next);
    }
}
