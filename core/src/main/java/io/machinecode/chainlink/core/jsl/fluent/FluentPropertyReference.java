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

import io.machinecode.chainlink.spi.jsl.inherit.InheritablePropertyReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class FluentPropertyReference<T extends FluentPropertyReference<T>> implements InheritablePropertyReference<T, FluentProperties> {

    private String ref;
    private FluentProperties properties;

    @Override
    public String getRef() {
        return this.ref;
    }

    @Override
    public T setRef(final String ref) {
        this.ref = ref;
        return (T)this;
    }

    @Override
    public FluentProperties getProperties() {
        return this.properties;
    }

    @Override
    public T setProperties(final FluentProperties properties) {
        this.properties = properties;
        return (T)this;
    }

    public T addProperty(final String name, final String value) {
        if (this.properties == null) {
            this.properties = new FluentProperties();
        }
        this.properties.addProperty(name, value);
        return (T)this;
    }

    @Override
    public T copy(final T that) {
        return PropertyReferenceTool.copy((T)this, that);
    }

    @Override
    public T merge(final T that) {
        return PropertyReferenceTool.merge((T)this, that);
    }
}
