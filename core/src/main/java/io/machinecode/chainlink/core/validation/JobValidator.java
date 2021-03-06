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
package io.machinecode.chainlink.core.validation;

import io.machinecode.chainlink.core.validation.execution.ExcecutionValidator;
import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.jsl.Job;
import io.machinecode.chainlink.spi.jsl.execution.Decision;
import io.machinecode.chainlink.spi.jsl.execution.Execution;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobValidator extends ValidatingVisitor<Job> {

    private static final JobValidator INSTANCE = new JobValidator();

    protected JobValidator() {
        super(Job.ELEMENT);
    }

    public static VisitorNode validate(final Job job) {
        final VisitorNode node = INSTANCE.visit(job);
        if (JobValidator.hasCycleOrInvalidTransition(node)) {
            throw new InvalidJobException(node);
        }
        return node;
    }

    public static void assertValid(final Job job) {
        final VisitorNode node = INSTANCE.visit(job);
        if (JobValidator.isInvalid(node)) {
            throw new InvalidJobException(node);
        }
    }

    @Override
    public void doVisit(final Job that, final VisitorNode node) {
        if (that.getId() == null) {
            node.addProblem(Messages.format("CHAINLINK-002102.validation.required.attribute", "id"));
        }
        if (!"1.0".equals(that.getVersion())) {
            node.addProblem(Messages.format("CHAINLINK-002104.validation.matches.attribute", "version", that.getVersion(), "1.0"));
        }

        if (that.getListeners() != null) {
            ListenersValidator.INSTANCE.visit(that.getListeners(), node);
        }
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.visit(that.getProperties(), node);
        }

        if (that.getExecutions() == null || that.getExecutions().isEmpty()) {
            node.addProblem(Messages.get("CHAINLINK-002108.validation.executions.required"));
        } else {
            if (that.getExecutions().get(0) instanceof Decision) {
                node.addProblem(Messages.format("CHAINLINK-002101.validation.decision.first.execution"));
            }
            for (final Execution execution : that.getExecutions()) {
                if (execution == null) {
                    node.addProblem(Messages.format("CHAINLINK-002100.validation.not.null.element", "execution"));
                    continue;
                }
                ExcecutionValidator.visit(execution, node);
            }
        }
    }
}
