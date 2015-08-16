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
package io.machinecode.chainlink.core.validation.execution;

import io.machinecode.chainlink.core.validation.ListenersValidator;
import io.machinecode.chainlink.core.validation.PropertiesValidator;
import io.machinecode.chainlink.core.validation.partition.PartitionValidator;
import io.machinecode.chainlink.core.validation.task.TaskValidator;
import io.machinecode.chainlink.core.validation.transition.TransitionValidator;
import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.jsl.execution.Step;
import io.machinecode.chainlink.spi.jsl.transition.Transition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class StepValidator extends ValidatingVisitor<Step> {

    public static final StepValidator INSTANCE = new StepValidator();

    protected StepValidator() {
        super(Step.ELEMENT);
    }

    @Override
    public void doVisit(final Step that, final VisitorNode context) {
        if (that.getId() == null) {
            context.addProblem(Messages.format("CHAINLINK-002102.validation.required.attribute", "id"));
        } else {
            context.addTransition(Messages.get("CHAINLINK-002301.validation.next.attribute"), that.getNext());
        }
        //if (that.getStartLimit() < 0) {
        //    context.addProblem(Problem.attributePositive("start-limit", that.getStartLimit()));
        //}

        if (that.getTransitions() != null) {
            for (final Object transition : that.getTransitions()) {
                if (transition == null) {
                    context.addProblem(Messages.format("CHAINLINK-002100.validation.not.null.element", "transition"));
                }
                TransitionValidator.visit((Transition) transition, context);
            }
        }

        if (that.getTask() != null) {
            TaskValidator.validate(that.getTask(), context);
        }
        if (that.getPartition() != null) {
            PartitionValidator.INSTANCE.visit(that.getPartition(), context);
        }
        if (that.getListeners() != null) {
            ListenersValidator.INSTANCE.visit(that.getListeners(), context);
        }
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.visit(that.getProperties(), context);
        }
    }
}
