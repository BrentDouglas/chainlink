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

import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;
import io.machinecode.chainlink.core.validation.transition.TransitionValidator;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.jsl.execution.Decision;
import io.machinecode.chainlink.spi.jsl.transition.Transition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DecisionValidator extends PropertyReferenceValidator<Decision> {

    public static final DecisionValidator INSTANCE = new DecisionValidator();

    protected DecisionValidator() {
        super(Decision.ELEMENT);
    }

    @Override
    public void doVisit(final Decision that, final VisitorNode context) {
        if (that.getId() == null) {
            context.addProblem(Messages.format("CHAINLINK-002102.validation.required.attribute", "id"));
        }

        if (that.getTransitions() != null) {
            for (final Transition transition : that.getTransitions()) {
                if (transition == null) {
                    context.addProblem(Messages.format("CHAINLINK-002100.validation.not.null.element", "transitions"));
                }
                TransitionValidator.visit(transition, context);
            }
        }
    }
}
