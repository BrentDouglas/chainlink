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

import io.machinecode.chainlink.spi.jsl.execution.Flow;
import io.machinecode.chainlink.spi.jsl.execution.Split;
import io.machinecode.chainlink.spi.jsl.inherit.Inheritable;
import io.machinecode.chainlink.spi.jsl.inherit.Rules;
import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableSplit<T extends InheritableSplit<T, F>,
        F extends Inheritable<F> & Flow>
        extends InheritableExecution<T>, Split {

    T setId(final String id);

    T setNext(final String next);

    @Override
    List<F> getFlows();

    T setFlows(final List<F> flows);

    class SplitTool {

        public static <T extends InheritableSplit<T, F>,
                F extends Inheritable<F> & Flow>
        T inherit(final T _this, final InheritableJobLoader repository, final String defaultJobXml) {
            final T copy = _this.copy();
            copy.setId(_this.getId());
            copy.setNext(_this.getNext());
            copy.setFlows(Rules.inheritingList(repository, defaultJobXml, _this.getFlows()));
            return copy;
        }

        public static <T extends InheritableSplit<T, F>,
                F extends Inheritable<F> & Flow>
        T copy(final T _this, final T that) {
            that.setId(_this.getId());
            that.setNext(_this.getNext());
            that.setFlows(Rules.copyList(_this.getFlows()));
            return that;
        }
    }
}
