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

import io.machinecode.chainlink.core.jsl.impl.JobImpl;
import io.machinecode.chainlink.core.jsl.impl.execution.ExecutionImpl;
import io.machinecode.chainlink.core.then.LinkAndRejectChain;
import io.machinecode.chainlink.core.then.LinkAndResolveChain;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class ExecutionCallback extends ExecutableImpl<ExecutionImpl> {

    private static final Logger log = Logger.getLogger(ExecutionCallback.class);

    final ExecutableId id;
    final JobImpl job;

    public ExecutionCallback(final JobImpl job, final ExecutableId id, final ExecutableId parentId, final ExecutableImpl<ExecutionImpl> executable, final WorkerId workerId) {
        super(parentId, executable, workerId);
        this.id = id;
        this.job = job;
    }

    @Override
    public ExecutableId getId() {
        return this.id;
    }

    @Override
    public void doExecute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId,
                          final ExecutableId parentId, final ExecutionContext previous) throws Throwable {
        Promise<Chain<?>,Throwable,?> next;
        try {
            if (chain.isCancelled()) {
                context.getJobContext().setBatchStatus(BatchStatus.STOPPING);
            }
            next = work.after(job, configuration, this.repositoryId, workerId, parentId, context, previous);
            next.onResolve(new LinkAndResolveChain(chain))
                    .onReject(chain)
                    .onCancel(chain);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023001.work.execution.after.exception", context));
            if (context.getStepContext() != null) {
                context.getStepContext().setBatchStatus(BatchStatus.FAILED);
            }
            context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            next = configuration.getTransport().callback(parentId, context);
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
