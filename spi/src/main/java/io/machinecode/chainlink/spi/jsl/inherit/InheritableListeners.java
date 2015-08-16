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

import io.machinecode.chainlink.spi.jsl.Listener;
import io.machinecode.chainlink.spi.jsl.Listeners;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableListeners<T extends InheritableListeners<T, L>,
        L extends Copyable<L> & Listener>
        extends MergeableList<T>, Listeners {

    @Override
    List<L> getListeners();

    T setListeners(final List<L> listeners);

    class ListenersTool {

        public static <T extends InheritableListeners<T, L>,
                L extends Copyable<L> & Listener>
        T copy(final T _this, final T that) {
            that.setListeners(Rules.copyList(_this.getListeners()));
            return that;
        }

        public static <T extends InheritableListeners<T, L>,
                L extends Copyable<L> & Listener>
        T merge(final T _this, final T that) {
            if (_this.getMerge()) {
                _this.setListeners(Rules.listRule(_this.getListeners(), that.getListeners()));
            }
            return _this;
        }
    }
}
