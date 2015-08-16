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
import io.machinecode.chainlink.core.util.Statuses;
import io.machinecode.chainlink.core.work.ExecutionExecutable;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.jsl.execution.Split;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.registry.SplitAccumulator;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.ResolvedDeferred;
import org.jboss.logging.Logger;

import java.util.List;


/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SplitImpl extends ExecutionImpl implements Split {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(SplitImpl.class);

    private final String next;
    private final List<FlowImpl> flows;

    public SplitImpl(final String id, final String next, final List<FlowImpl> flows) {
        super(id);
        this.next = next;
        this.flows = flows;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
    public List<FlowImpl> getFlows() {
        return this.flows;
    }

    @Override
    public Promise<Chain<?>,Throwable,?> before(final JobImpl job, final Configuration configuration, final RepositoryId repositoryId,
                           final WorkerId workerId, final ExecutableId callbackId, final ExecutableId parentId,
                           final ExecutionContextImpl context) throws Exception {
        log.debugf(Messages.get("CHAINLINK-021000.split.before"), context, this.id);
        if (Statuses.isStopping(context) || Statuses.isComplete(context)) {
            return configuration.getTransport().callback(parentId, context);
        }
        final ExecutionExecutable[] flows = new ExecutionExecutable[this.flows.size()];
        for (int i = 0; i < flows.length; ++i) {
            final FlowImpl flow = this.flows.get(i);
            final ExecutionContextImpl flowContext = new ExecutionContextImpl(
                    new JobContextImpl(context.getJobContext()),
                    null,
                    context.getJobExecutionId(),
                    context.getRestartJobExecutionId(),
                    context.getRestartElementId(),
                    null
            );
            flows[i] = new ExecutionExecutable(
                    job,
                    callbackId,
                    flow,
                    flowContext,
                    repositoryId,
                    null
            );
        }
        return configuration.getTransport().distribute(
                flows.length,
                flows
        );
    }

    @Override
    public Promise<Chain<?>,Throwable,?> after(final JobImpl job, final Configuration configuration, final RepositoryId repositoryId,
                          final WorkerId workerId, final ExecutableId parentId, final ExecutionContextImpl context,
                          final ExecutionContext childContext) throws Exception {
        log.debugf(Messages.get("CHAINLINK-021001.split.after"), context, this.id);
        final long jobExecutionId = context.getJobExecutionId();
        final Long stepExecutionId = childContext.getLastStepExecutionId();
        final SplitAccumulator accumulator = configuration.getRegistry().getSplitAccumulator(jobExecutionId, this.id);
        if (stepExecutionId != null) {
            accumulator.addPriorStepExecutionId(stepExecutionId);
        }
        if (accumulator.incrementAndGetCallbackCount() < this.flows.size()) {
            return new ResolvedDeferred<Chain<?>, Throwable, Object>(null);
        }
        context.setPriorStepExecutionId(accumulator.getPriorStepExecutionIds());
        if (Statuses.isStopping(context) || Statuses.isComplete(context)) {
            return configuration.getTransport().callback(parentId, context);
        }
        return this.next(job, configuration, workerId, context, parentId, repositoryId, this.next, null);
    }

    @Override
    protected Logger log() {
        return log;
    }
}
