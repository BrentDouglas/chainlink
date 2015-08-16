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
package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.core.context.JobContextImpl;
import io.machinecode.chainlink.core.context.StepContextImpl;
import io.machinecode.chainlink.core.jsl.impl.task.TaskWork;
import io.machinecode.chainlink.core.then.LinkAndRejectChain;
import io.machinecode.chainlink.core.then.LinkAndResolveChain;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.OnCancel;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class TaskExecutable extends ExecutableImpl<TaskWork> {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(TaskExecutable.class);

    final int timeout;

    public TaskExecutable(final ExecutableId parentId, final TaskWork work, final ExecutionContextImpl context, final RepositoryId repositoryId, final int timeout) {
        super(parentId, context, work, repositoryId, null);
        this.timeout = timeout;
    }

    @Override
    public void doExecute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId,
                          final ExecutableId parentId, final ExecutionContext previous) throws Throwable {
        //TODO Check ordering of this and check it is allowed to run in this thread
        chain.onCancel(new OnCancel() {
            @Override
            public boolean cancel(final boolean interrupt) {
                log.debugf(Messages.format("CHAINLINK-023005.work.task.cancel", context));
                work.cancel(configuration, context);
                return true;
            }
        });
        final StepContextImpl stepContext = context.getStepContext();
        final JobContextImpl jobContext = context.getJobContext();
        try {
            work.run(configuration, chain, this.repositoryId, this.context, timeout);
            final Promise<Chain<?>,Throwable,?> next = configuration.getTransport().callback(parentId, this.context);
            next.onResolve(new LinkAndResolveChain(chain))
                    .onReject(chain)
                    .onCancel(chain);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023004.work.task.run.exception", this.context));
            if (e instanceof Exception) {
                stepContext.setException((Exception) e);
            }
            stepContext.setBatchStatus(BatchStatus.FAILED);
            jobContext.setBatchStatus(BatchStatus.FAILED);
            final Promise<Chain<?>,Throwable,?> next = configuration.getTransport().callback(parentId, this.context);
            next.onResolve(new LinkAndRejectChain(chain, e))
                    .onReject(chain)
                    .onCancel(chain);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
