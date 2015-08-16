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
package io.machinecode.chainlink.rt.glassfish.schema;

import io.machinecode.chainlink.core.util.Mutable;
import io.machinecode.chainlink.core.util.Op;
import io.machinecode.chainlink.core.schema.MutablePropertySchema;
import io.machinecode.chainlink.core.schema.PropertySchema;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Configured;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Configured
public interface GlassfishProperty extends ConfigBeanProxy, MutablePropertySchema, Hack<PropertySchema> {

    @Attribute("name")
    String getName();

    void setName(final String name);

    @Attribute("value")
    String getValue();

    void setValue(final String value);

    class Duck implements Mutable<PropertySchema> {

        private final GlassfishProperty to;

        public Duck(final GlassfishProperty to) {
            this.to = to;
        }

        @Override
        public boolean willAccept(final PropertySchema from) {
            return to.getName().equals(from.getName());
        }

        @Override
        public void accept(final PropertySchema from, final Op... ops) {
            to.setName(from.getName());
            to.setValue(from.getValue());
        }
    }
}
