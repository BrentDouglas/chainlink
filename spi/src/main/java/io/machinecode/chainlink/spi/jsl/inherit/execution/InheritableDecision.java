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
package io.machinecode.chainlink.spi.jsl.inherit.execution;

import io.machinecode.chainlink.spi.jsl.Properties;
import io.machinecode.chainlink.spi.jsl.execution.Decision;
import io.machinecode.chainlink.spi.jsl.inherit.Copyable;
import io.machinecode.chainlink.spi.jsl.inherit.Mergeable;
import io.machinecode.chainlink.spi.jsl.inherit.Rules;
import io.machinecode.chainlink.spi.jsl.transition.Transition;
import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableDecision<T extends InheritableDecision<T, U, V>,
        U extends Mergeable<U> & Properties,
        V extends Copyable & Transition>
        extends InheritableExecution<T>, Decision {

    T setId(final String id);

    T setRef(final String ref) ;

    @Override
    U getProperties();

    T setProperties(final U properties);

    @Override
    List<V> getTransitions();

    T setTransitions(final List<V> transitions);

    /**
     * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
     */
    class DecisionTool {

        public static <T extends InheritableDecision<T, U, V>,
                U extends Mergeable<U> & Properties,
                V extends Copyable & Transition>
        T inherit(final T _this, final InheritableJobLoader repository, final String defaultJobXml) {
            return _this.copy();
        }

        public static <T extends InheritableDecision<T, U, V>,
                U extends Mergeable<U> & Properties,
                V extends Copyable & Transition>
        T copy(final T _this, final T that) {
            that.setId(_this.getId());
            that.setRef(_this.getRef());
            that.setProperties(Rules.copy(_this.getProperties()));
            that.setTransitions(Rules.copyList(_this.getTransitions()));
            return that;
        }
    }
}
