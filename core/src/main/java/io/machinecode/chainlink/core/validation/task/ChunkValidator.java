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

import io.machinecode.chainlink.core.util.Strings;
import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.jsl.task.Chunk;
import io.machinecode.chainlink.spi.jsl.task.Chunk.CheckpointPolicy;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChunkValidator extends ValidatingVisitor<Chunk> {

    public static final ChunkValidator INSTANCE = new ChunkValidator();

    protected ChunkValidator() {
        super(Chunk.ELEMENT);
    }

    @Override
    public void doVisit(final Chunk that, final VisitorNode context) {
        if (that.getCheckpointPolicy() == null) {
            context.addProblem(Messages.format("CHAINLINK-002102.validation.required.attribute", "checkpoint-policy"));
        }
        if (!CheckpointPolicy.ITEM.equals(that.getCheckpointPolicy())
                && !CheckpointPolicy.CUSTOM.equals(that.getCheckpointPolicy())) {
            context.addProblem(Messages.format("CHAINLINK-002104.validation.matches.attribute", "checkpoint-policy", that.getCheckpointPolicy(), Strings.join(',', CheckpointPolicy.ITEM, CheckpointPolicy.CUSTOM)));
        }

        //if (that.getItemCount() < 0) {
        //    context.addProblem(Problem.attributePositive("item-count", that.getItemCount()));
        //}
        //if (that.getTimeLimit() < 0) {
        //    context.addProblem(Problem.attributePositive("time-limit", that.getTimeLimit()));
        //}
        //if (that.getSkipLimit() < 0) {
        //    context.addProblem(Problem.attributePositive("skip-limit", that.getSkipLimit()));
        //}
        //if (that.getRetryLimit() < 0) {
        //    context.addProblem(Problem.attributePositive("retry-limit", that.getRetryLimit()));
        //}

        if (that.getReader() == null) {
            context.addProblem(Messages.format("CHAINLINK-002100.validation.not.null.element", "reader"));
        } else {
            ItemReaderValidator.INSTANCE.visit(that.getReader(), context);
        }
        if (that.getWriter() == null) {
            context.addProblem(Messages.format("CHAINLINK-002100.validation.not.null.element", "writer"));
        } else {
            ItemWriterValidator.INSTANCE.visit(that.getWriter(), context);
        }
        if (that.getProcessor() != null) {
            ItemProcessorValidator.INSTANCE.visit(that.getProcessor(), context);
        }

        //Nullable

        if (that.getCheckpointAlgorithm() != null) {
            CheckpointAlgorithmValidator.INSTANCE.visit(that.getCheckpointAlgorithm(), context);
        }
        if (that.getNoRollbackExceptionClasses() != null) {
            ExecutionClassFilterValidator.NO_ROLLBACK.visit(that.getNoRollbackExceptionClasses(), context);
        }
        if (that.getSkippableExceptionClasses() != null) {
            ExecutionClassFilterValidator.SKIPPABLE.visit(that.getSkippableExceptionClasses(), context);
        }
        if (that.getRetryableExceptionClasses() != null) {
            ExecutionClassFilterValidator.RETRYABLE.visit(that.getRetryableExceptionClasses(), context);
        }
    }
}
