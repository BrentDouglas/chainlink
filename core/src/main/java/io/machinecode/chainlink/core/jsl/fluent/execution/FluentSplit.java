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

import io.machinecode.chainlink.spi.jsl.inherit.execution.InheritableSplit;
import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentSplit implements FluentExecution<FluentSplit>, InheritableSplit<FluentSplit, FluentFlow> {

    private String id;
    private String next;
    private List<FluentFlow> flows = new ArrayList<>(0);


    @Override
    public String getId() {
        return this.id;
    }

    public FluentSplit setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    public FluentSplit setNext(final String next) {
        this.next = next;
        return this;
    }

    @Override
    public List<FluentFlow> getFlows() {
        return this.flows;
    }

    @Override
    public FluentSplit setFlows(final List<FluentFlow> flows) {
        this.flows = flows;
        return this;
    }

    public FluentSplit addFlow(final FluentFlow flow) {
        this.flows.add(flow);
        return this;
    }

    @Override
    public FluentSplit inherit(final InheritableJobLoader repository, final String defaultJobXml) {
        return SplitTool.inherit(this, repository, defaultJobXml);
    }

    @Override
    public FluentSplit copy() {
        return copy(new FluentSplit());
    }

    @Override
    public FluentSplit copy(final FluentSplit that) {
        return SplitTool.copy(this, that);
    }
}
