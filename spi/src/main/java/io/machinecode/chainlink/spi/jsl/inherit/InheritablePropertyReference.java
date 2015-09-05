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
package io.machinecode.chainlink.spi.jsl.inherit;

import io.machinecode.chainlink.spi.jsl.Properties;
import io.machinecode.chainlink.spi.jsl.PropertyReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritablePropertyReference<T extends InheritablePropertyReference<T, P>,
        P extends Mergeable<P> & Properties>
        extends Mergeable<T>, PropertyReference {

    T setRef(final String ref);

    @Override
    P getProperties();

    T setProperties(final P properties);

    class PropertyReferenceTool {

        public static <T extends InheritablePropertyReference<T, P>,
                P extends Mergeable<P> & Properties>
        T copy(final T _this, final T that) {
            that.setRef(_this.getRef());
            that.setProperties(Rules.copy(_this.getProperties()));
            return that;
        }

        public static <T extends InheritablePropertyReference<T, P>,
                P extends Mergeable<P> & Properties>
        T merge(final T _this, final T that) {
            _this.setRef(Rules.attributeRule(_this.getRef(), that.getRef()));
            _this.setProperties(Rules.merge(_this.getProperties(), that.getProperties()));
            return _this;
        }
    }

}
