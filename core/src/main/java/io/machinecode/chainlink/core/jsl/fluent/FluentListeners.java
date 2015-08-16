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
package io.machinecode.chainlink.core.jsl.fluent;


import io.machinecode.chainlink.spi.jsl.inherit.InheritableListeners;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentListeners extends FluentMergeableList<FluentListeners> implements InheritableListeners<FluentListeners, FluentListener> {

    private boolean merge;
    private List<FluentListener> listeners = new ArrayList<>(0);

    @Override
    public List<FluentListener> getListeners() {
        return this.listeners;
    }

    @Override
    public FluentListeners setListeners(final List<FluentListener> listeners) {
        this.listeners = listeners;
        return this;
    }

    public FluentListeners addListener(final FluentListener listener) {
        this.listeners.add(listener);
        return this;
    }

    @Override
    public boolean getMerge() {
        return merge;
    }

    @Override
    public FluentListeners setMerge(final boolean merge) {
        this.merge = merge;
        return this;
    }

    @Override
    public FluentListeners copy() {
        return copy(new FluentListeners());
    }

    @Override
    public FluentListeners copy(final FluentListeners that) {
        return ListenersTool.copy(this, that);
    }

    @Override
    public FluentListeners merge(final FluentListeners that) {
        return ListenersTool.merge(this, that);
    }
}
