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

import io.machinecode.chainlink.spi.jsl.inherit.InheritableProperty;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentProperty implements InheritableProperty<FluentProperty> {

    private String name;
    private String value;

    @Override
    public String getName() {
            return this.name;
    }

    public FluentProperty setName(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public FluentProperty setValue(final String value) {
        this.value = value;
        return this;
    }

    @Override
    public FluentProperty copy() {
        return copy(new FluentProperty());
    }

    @Override
    public FluentProperty copy(final FluentProperty that) {
        return PropertyTool.copy(this, that);
    }
}
