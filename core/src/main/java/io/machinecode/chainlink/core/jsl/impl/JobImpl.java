/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.jsl.impl;

import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.core.execution.ExecutableEventImpl;
import io.machinecode.chainlink.core.jsl.impl.execution.ExecutionImpl;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.util.Repo;
import io.machinecode.chainlink.core.util.Statuses;
import io.machinecode.chainlink.core.validation.InvalidJobException;
import io.machinecode.chainlink.core.validation.JobTraversal;
import io.machinecode.chainlink.core.validation.JobValidator;
import io.machinecode.chainlink.core.work.ExecutionExecutable;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.jsl.Job;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.ResolvedDeferred;
import org.jboss.logging.Logger;

import javax.batch.api.listener.JobListener;
import javax.batch.runtime.BatchStatus;
import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobImpl implements Job, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(JobImpl.class);

    private final String id;
    private final String version;
    private final String restartable;
    private final PropertiesImpl properties;
    private final ListenersImpl listeners;
    private final List<ExecutionImpl> executions;
    private final JobTraversal traversal;

    private transient List<ListenerImpl> _listeners;

    public JobImpl(final String id, final String version, final String restartable, final PropertiesImpl properties,
                   final ListenersImpl listeners, final List<ExecutionImpl> executions) throws InvalidJobException {
        this.id = id;
        this.version = version;
        this.restartable = restartable;
        this.properties = properties;
        this.listeners = listeners;
        this.executions = executions;
        this.traversal = new JobTraversal(id, JobValidator.validate(this));
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

    private List<ListenerImpl> _listeners(final Configuration configuration, final ExecutionContext context) throws Exception {
        if (this._listeners == null) {
            this._listeners = this.listeners.getListenersImplementing(configuration, context, JobListener.class);
        }
        return _listeners;
    }

    public Promise<Chain<?>,Throwable,?> before(final Configuration configuration, final RepositoryId repositoryId,
                              final ExecutableId callbackId, final ExecutionContextImpl context) throws Exception {
        final Repository repository = Repo.getRepository(configuration, repositoryId);
        long jobExecutionId = context.getJobExecutionId();
        Repo.startedJob(repository, jobExecutionId);
        log.debugf(Messages.get("CHAINLINK-018000.job.create.job.context"), context);
        Exception exception = null;
        for (final ListenerImpl listener : this._listeners(configuration, context)) {
            try {
                log.debugf(Messages.get("CHAINLINK-018001.job.listener.before.job"), context);
                listener.beforeJob(configuration, context);
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

        final BatchStatus batchStatus = context.getJobContext().getBatchStatus();
        if (Statuses.isStopping(batchStatus)) {
            log.debugf(Messages.get("CHAINLINK-018002.job.status.early.termination"), context, batchStatus);
            return configuration.getTransport().callback(callbackId, context);
        }

        final Long restartJobExecutionId = context.getRestartJobExecutionId();
        if (restartJobExecutionId == null) {
            return _runNext(configuration, callbackId, context, repositoryId, this.executions.get(0));
        }
        repository.linkJobExecutions(jobExecutionId, restartJobExecutionId);
        final String restartId = context.getRestartElementId();
        if (restartId == null) {
            return _runNext(configuration, callbackId, context, repositoryId, this.executions.get(0));
        }
        log.debugf(Messages.get("CHAINLINK-018004.job.restart.transition"), context, restartId);
        return _runNext(configuration, callbackId, context, repositoryId, traversal.next(restartId));
    }

    public void after(final Configuration configuration, final ExecutionContext context) throws Exception {
        Exception exception = null;
        for (final ListenerImpl listener : this._listeners(configuration, context)) {
            try {
                log.debugf(Messages.get("CHAINLINK-018003.job.listener.after.job"), context);
                listener.afterJob(configuration, context);
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

    public ExecutionImpl getNextExecution(final String next) {
        return traversal.next(next);
    }

    private Promise<Chain<?>,Throwable,?> _runNext(final Configuration configuration, final ExecutableId callbackId,
                                        final ExecutionContextImpl context, final RepositoryId repositoryId,
                                        final ExecutionImpl next) throws Exception {
        return new ResolvedDeferred<Chain<?>, Throwable, Object>(execute(configuration, new ExecutionExecutable(
                this,
                callbackId,
                next,
                context,
                repositoryId,
                null
        )));
    }

    public static Chain<?> execute(final Configuration configuration, final Executable executable) throws Exception {
        final Transport transport = configuration.getTransport();
        final Registry registry = configuration.getRegistry();
        final Executor executor = configuration.getExecutor();
        final Chain<?> chain = new ChainImpl<Void>();
        final ChainId chainId = new UUIDId(transport);
        registry.registerChain(executable.getContext().getJobExecutionId(), chainId, chain);
        final WorkerId workerId = executable.getWorkerId();
        final Worker worker;
        if (workerId == null) {
            worker = executor.getWorker();
        } else {
            worker = executor.getWorker(workerId);
        }
        worker.execute(new ExecutableEventImpl(executable, chainId));
        return chain;
    }
}
