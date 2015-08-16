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
package io.machinecode.chainlink.spi.jsl.inherit;

import io.machinecode.chainlink.spi.jsl.Properties;
import io.machinecode.chainlink.spi.jsl.Property;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableProperties<T extends InheritableProperties<T, P>,
        P extends Copyable<P> & Property>
        extends MergeableList<T>, Properties {

    T setPartition(final String partition);

    @Override
    List<P> getProperties();

    T setProperties(final List<P> properties);

    class PropertiesTool {

        public static <T extends InheritableProperties<T, P>,
                P extends Copyable<P> & Property>
        T copy(final T _this, final T that) {
            that.setPartition(_this.getPartition());
            that.setProperties(Rules.copyList(_this.getProperties()));
            return that;
        }

        public static <T extends InheritableProperties<T, P>,
                P extends Copyable<P> & Property>
        T merge(final T _this, final T that) {
            _this.setPartition(Rules.attributeRule(_this.getPartition(), that.getPartition()));
            if (_this.getMerge()) {
                _this.setProperties(Rules.listRule(_this.getProperties(), that.getProperties()));
            }
            return _this;
        }
    }
}
