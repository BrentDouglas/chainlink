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
package io.machinecode.chainlink.core.expression;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
class PropertyResolver extends Resolver {
    final Properties properties;

    PropertyResolver(final String prefix, final int length, final Properties properties) {
        super(prefix, length);
        this.properties = properties;
    }

    CharSequence resolve(final CharSequence value) {
        if (this.properties == null) {
            return EMPTY;
        }
        final String that = this.properties.getProperty(value.toString());
        return that == null ? EMPTY : that;
    }
}
