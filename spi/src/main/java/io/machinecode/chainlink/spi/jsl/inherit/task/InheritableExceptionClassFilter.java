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
package io.machinecode.chainlink.spi.jsl.inherit.task;

import io.machinecode.chainlink.spi.jsl.inherit.Copyable;
import io.machinecode.chainlink.spi.jsl.inherit.MergeableList;
import io.machinecode.chainlink.spi.jsl.inherit.Rules;
import io.machinecode.chainlink.spi.jsl.task.ExceptionClass;
import io.machinecode.chainlink.spi.jsl.task.ExceptionClassFilter;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableExceptionClassFilter<T extends InheritableExceptionClassFilter<T, E>,
        E extends Copyable<E> & ExceptionClass>
        extends MergeableList<T>, ExceptionClassFilter {

    @Override
    List<E> getIncludes();

    T setIncludes(final List<E> includes);

    @Override
    List<E> getExcludes();

    T setExcludes(final List<E> excludes);

    class ExceptionClassFilterTool {

        public static <T extends InheritableExceptionClassFilter<T, E>,
                E extends Copyable<E> & ExceptionClass>
        T copy(final T _this, final T that) {
            that.setIncludes(Rules.copyList(_this.getIncludes()));
            that.setExcludes(Rules.copyList(_this.getExcludes()));
            return that;
        }

        public static <T extends InheritableExceptionClassFilter<T, E>,
                E extends Copyable<E> & ExceptionClass>
        T merge(final T _this, final T that) {
            if (_this.getMerge()) {
                _this.setIncludes(Rules.listRule(_this.getIncludes(), that.getIncludes()));
                _this.setExcludes(Rules.listRule(_this.getExcludes(), that.getExcludes()));
            }
            return _this;
        }
    }
}
