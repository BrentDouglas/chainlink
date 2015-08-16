/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package io.machinecode.chainlink.core.jsl.impl.execution;

import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.core.context.JobContextImpl;
import io.machinecode.chainlink.core.jsl.impl.JobImpl;
import io.machinecode.chainlink.core.jsl.impl.transition.TransitionImpl;
import io.machinecode.chainlink.core.util.Statuses;
import io.machinecode.chainlink.core.work.ExecutionExecutable;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.ResolvedDeferred;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class ExecutionImpl implements io.machinecode.chainlink.spi.jsl.execution.Execution, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(ExecutionImpl.class);

    protected final String id;

    public ExecutionImpl(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public abstract Promise<Chain<?>,Throwable,?> before(final JobImpl job, final Configuration configuration, final RepositoryId repositoryId,
                                         final WorkerId workerId, final ExecutableId callbackId, final ExecutableId parentId,
                                         final ExecutionContextImpl context) throws Exception;

    public abstract Promise<Chain<?>,Throwable,?> after(final JobImpl job, final Configuration configuration, final RepositoryId repositoryId,
                                        final WorkerId workerId, final ExecutableId parentId, final ExecutionContextImpl context,
                                        final ExecutionContext childContext) throws Exception;

    public TransitionImpl transition(final ExecutionContextImpl context, final List<? extends TransitionImpl> transitions,
                                     final BatchStatus batchStatus, final String exitStatus) throws Exception {
        final String actualStatus = exitStatus == null ? batchStatus.name() : exitStatus;
        log().tracef(Messages.get("CHAINLINK-009103.execution.transition.statuses"), context, id, exitStatus);
        for (final TransitionImpl transition : transitions) {
            if (Statuses.matches(transition.getOn(), actualStatus)) {
                log().tracef(Messages.get("CHAINLINK-009101.execution.transition.matched"), context, id, transition.element(), actualStatus, transition.getOn());
                if (transition.isTerminating()) {
                    final JobContextImpl jobContext = context.getJobContext();
                    jobContext.setBatchStatus(transition.getBatchStatus());
                    final String transitionExitStatus = transition.getExitStatus();
                    if (transitionExitStatus != null) {
                        jobContext.setExitStatus(transitionExitStatus);
                    }
                    context.setRestartElementId(transition.getRestartId());
                }
                return transition;
            } else {
                log().tracef(Messages.get("CHAINLINK-009102.execution.transition.skipped"), context, id, transition.element(), actualStatus, transition.getOn());
            }
        }
        log().tracef(Messages.get("CHAINLINK-009104.execution.no.transition.matched"), context, id, actualStatus);
        return null;
    }

    public Promise<Chain<?>,Throwable,?> next(final JobImpl job, final Configuration configuration, final WorkerId workerId, final ExecutionContextImpl context,
                         final ExecutableId parentId, final RepositoryId repositoryId, final String next,
                         final TransitionImpl transition) throws Exception {
        final JobContextImpl jobContext = context.getJobContext();
        final BatchStatus batchStatus = jobContext.getBatchStatus();
        if (Statuses.isStopping(context) || Statuses.isFailed(batchStatus)) {
            return configuration.getTransport().callback(parentId, context);
        }
        if (transition != null && transition.getNext() != null) {
            log().debugf(Messages.get("CHAINLINK-009100.execution.transition"), context, id, transition.getNext());
            return _runNextExecution(job, configuration, parentId, context, workerId, repositoryId, transition.getNext());
        } else if (next != null) {
            log().debugf(Messages.get("CHAINLINK-009100.execution.transition"), context, id, next);
            return _runNextExecution(job, configuration, parentId, context, workerId, repositoryId, next);
        } else {
            return configuration.getTransport().callback(parentId, context);
        }
    }

    private Promise<Chain<?>,Throwable,?> _runNextExecution(final JobImpl job, final Configuration configuration, final ExecutableId parentId, final ExecutionContextImpl context,
                                       final WorkerId workerId, final RepositoryId repositoryId, final String next) throws Exception {
        final ExecutionImpl execution = job.getNextExecution(next);
        return _resolve(JobImpl.execute(configuration, new ExecutionExecutable(
                job,
                parentId,
                execution,
                context,
                repositoryId,
                workerId
        )));
    }

    protected Logger log() {
        return log;
    }

    protected ResolvedDeferred<Chain<?>,Throwable,Object> _resolve(final Chain<?> chain) {
        return new ResolvedDeferred<Chain<?>,Throwable,Object>(chain);
    }
}
