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
package io.machinecode.chainlink.inject.guice;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface BindingProvider {

    List<Binding> getBindings();

    class Binding {
        final Class<?> satisfies;
        final String name;
        final Class<?> type;

        public Binding(final Class<?> satisfies, final String name, final Class<?> type) {
            this.satisfies = satisfies;
            this.name = name;
            this.type = type;
        }

        public Class<?> getSatisfies() {
            return satisfies;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public static Binding of(final Class<?> satisfies, final String name, final Class<?> type) {
            return new Binding(satisfies, name, type);
        }
    }
}
