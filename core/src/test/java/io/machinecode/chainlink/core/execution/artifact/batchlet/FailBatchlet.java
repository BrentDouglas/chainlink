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
package io.machinecode.chainlink.core.execution.artifact.batchlet;

import io.machinecode.chainlink.core.base.Reference;
import io.machinecode.chainlink.core.execution.artifact.exception.FailProcessException;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class FailBatchlet extends javax.batch.api.AbstractBatchlet {

    public static final Reference<Boolean> hasRun = new Reference<>(false);

    @Override
    public String process() throws Exception {
        hasRun.set(true);
        throw new FailProcessException();
    }

    public static void reset() {
        hasRun.set(false);
    }
}
