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
package io.machinecode.chainlink.spi.jsl.inherit.task;

import io.machinecode.chainlink.spi.jsl.inherit.Mergeable;
import io.machinecode.chainlink.spi.jsl.inherit.Rules;
import io.machinecode.chainlink.spi.jsl.task.ExceptionClass;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableExceptionClass<T extends InheritableExceptionClass<T>>
        extends Mergeable<T>, ExceptionClass {

    T setClassName(final String className);

    class ExceptionClassTool {

        public static <T extends InheritableExceptionClass<T>>
        T copy(final T _this, final T that) {
            that.setClassName(_this.getClassName());
            return that;
        }

        public static <T extends InheritableExceptionClass<T>>
        T merge(final T _this, final T that) {
            _this.setClassName(Rules.attributeRule(_this.getClassName(), that.getClassName()));
            return _this;
        }
    }
}
