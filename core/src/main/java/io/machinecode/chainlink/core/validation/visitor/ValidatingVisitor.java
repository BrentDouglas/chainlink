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
package io.machinecode.chainlink.core.validation.visitor;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.core.util.ImmutablePair;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.jsl.Element;

import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class ValidatingVisitor<T extends Element> extends Visitor<T> {

    protected ValidatingVisitor(final String element) {
        super(element);
    }

    public static boolean hasCycleOrInvalidTransition(final VisitorNode node) {
        boolean ret = false;
        //Root node is a job
        for (final VisitorNode execution : node.children) {
            final VisitorNode.Cycle cycle = new VisitorNode.Cycle();
            final Set<String> trail = new THashSet<>();
            ret = _hasCycleOrInvalidTransition(execution, cycle, trail) || ret;
        }
        return ret;
    }

    private static boolean _hasCycleOrInvalidTransition(final VisitorNode node, final VisitorNode.Cycle cycle, final Set<String> trail) {
        boolean ret = false;
        if (node.id != null) {
            if (!trail.add(node.id)) {
                for (int i = cycle.size() - 1; i >= 0; --i) {
                    final VisitorNode that = cycle.get(i).getValue();
                    if (that.equals(node)) {
                        that.addCycle(cycle.subList(i, cycle.size()));
                        ret = true;
                        break;
                    }
                }
                if (!ret) {
                    cycle.get(0).getValue().addCycle(cycle);
                    ret = true;
                }
            } else {
                for (final Transition transition : node.transitions) {
                    final VisitorNode execution = node.getExecution(transition);
                    if (execution == null) {
                        node.addProblem(Messages.format("CHAINLINK-002107.validation.invalid.transition", node.id, transition.to));
                        ret = true;
                        continue;
                    }
                    final VisitorNode.Cycle subCycle = new VisitorNode.Cycle(cycle);
                    final THashSet<String> subTrail = new THashSet<String>(trail);
                    subCycle.add(ImmutablePair.of(transition, node));
                    ret = _hasCycleOrInvalidTransition(
                            execution,
                            subCycle,
                            subTrail
                    ) || ret;
                }
            }
        }
        for (final VisitorNode execution : node.children) {
            ret = _hasCycleOrInvalidTransition(execution, new VisitorNode.Cycle(), new THashSet<String>()) || ret;
        }
        return ret;
    }

    public static boolean isInvalid(final VisitorNode node) {
        if (node.failed) {
            return true;
        }
        for (final VisitorNode child : node.children) {
            if (isInvalid(child)) {
                return true;
            }
        }
        return false;
    }
}