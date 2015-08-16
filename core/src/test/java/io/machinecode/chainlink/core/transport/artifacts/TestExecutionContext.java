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
package io.machinecode.chainlink.core.transport.artifacts;

import io.machinecode.chainlink.core.context.JobContextImpl;
import io.machinecode.chainlink.core.context.StepContextImpl;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.Item;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestExecutionContext implements ExecutionContext {

    final long jobExecutionId;

    public TestExecutionContext(final long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public Long getRestartJobExecutionId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isRestarting() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getJobExecutionId() {
        return jobExecutionId;
    }

    @Override
    public Long getStepExecutionId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long getPartitionExecutionId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JobContextImpl getJobContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StepContextImpl getStepContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Item[] getItems() {
        return new Item[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRestartElementId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long getLastStepExecutionId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long[] getPriorStepExecutionIds() {
        return new long[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
