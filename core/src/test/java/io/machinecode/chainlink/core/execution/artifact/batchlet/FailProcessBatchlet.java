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
import io.machinecode.chainlink.core.execution.artifact.exception.FailProcessException;
import io.machinecode.chainlink.core.execution.artifact.exception.FailStopException;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class FailProcessBatchlet extends javax.batch.api.AbstractBatchlet {

    public static final Reference<Boolean> hasStopped = new Reference<>(false);

    @Override
    public String process() throws Exception {
        synchronized (this) {
            while (!hasStopped.get()) {
                this.wait();
            }
        }
        throw new FailProcessException();
    }

    @Override
    public void stop() throws Exception {
        hasStopped.set(true);
        synchronized (this) {
            this.notifyAll();
        }
        throw new FailStopException();
    }

    public static void reset() {
        hasStopped.set(false);
    }
}
