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
package io.machinecode.chainlink.core.jsl.fluent.execution;

import io.machinecode.chainlink.core.jsl.fluent.FluentProperties;
import io.machinecode.chainlink.core.jsl.fluent.FluentPropertyReference;
import io.machinecode.chainlink.core.jsl.fluent.transition.FluentTransition;
import io.machinecode.chainlink.spi.jsl.inherit.execution.InheritableDecision;
import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentDecision extends FluentPropertyReference<FluentDecision> implements FluentExecution<FluentDecision>, InheritableDecision<FluentDecision, FluentProperties, FluentTransition> {

    private String id;
    private List<FluentTransition> transitions = new ArrayList<>(0);

    @Override
    public String getId() {
        return this.id;
    }

    public FluentDecision setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public List<FluentTransition> getTransitions() {
        return this.transitions;
    }

    @Override
    public FluentDecision setTransitions(final List<FluentTransition> transitions) {
        this.transitions = transitions;
        return this;
    }

    public FluentDecision addTransition(final FluentTransition transition) {
        this.transitions.add(transition);
        return this;
    }

    @Override
    public FluentDecision inherit(final InheritableJobLoader repository, final String defaultJobXml) {
        return DecisionTool.inherit(this, repository, defaultJobXml);
    }

    @Override
    public FluentDecision copy() {
        return copy(new FluentDecision());
    }
}
