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
package io.machinecode.chainlink.core.execution.artifact.batchlet;

import io.machinecode.chainlink.core.base.Reference;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class OverrideBatchlet extends javax.batch.api.AbstractBatchlet {

    public static final Reference<Boolean> hasRun = new Reference<>(false);

    @Inject
    private StepContext stepContext;

    @Inject
    private JobContext jobContext;

    @Override
    public String process() throws Exception {
        hasRun.set(true);
        stepContext.setExitStatus("Step Exit Status");
        jobContext.setExitStatus("Job Exit Status");
        return BatchStatus.COMPLETED.toString();
    }

    public static void reset() {
        hasRun.set(false);
    }
}
