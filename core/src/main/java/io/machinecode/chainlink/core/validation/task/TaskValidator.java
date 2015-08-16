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
package io.machinecode.chainlink.core.validation.task;

import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.jsl.task.Batchlet;
import io.machinecode.chainlink.spi.jsl.task.Chunk;
import io.machinecode.chainlink.spi.jsl.task.Task;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public final class TaskValidator {

    private TaskValidator(){}

    public static void validate(final Task that, final VisitorNode context) {
        if (that instanceof Batchlet) {
            BatchletValidator.INSTANCE.visit((Batchlet) that, context);
        } else if (that instanceof Chunk) {
            ChunkValidator.INSTANCE.visit((Chunk) that, context);
        }
    }
}
