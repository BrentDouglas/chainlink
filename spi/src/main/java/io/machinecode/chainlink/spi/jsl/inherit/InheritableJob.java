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

import io.machinecode.chainlink.spi.jsl.Job;
import io.machinecode.chainlink.spi.jsl.Listeners;
import io.machinecode.chainlink.spi.jsl.Properties;
import io.machinecode.chainlink.spi.jsl.execution.Execution;
import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableJob<T extends InheritableJob<T, P, L, E>,
        P extends Mergeable<P> & Properties,
        L extends Mergeable<L> & Listeners,
        E extends Inheritable & Execution>
        extends InheritableBase<T>, Job {

    T setId(final String id);

    T setRestartable(final String restartable);

    @Override
    P getProperties();

    T setProperties(final P properties);

    @Override
    L getListeners();

    T setListeners(final L listeners);

    @Override
    List<E> getExecutions();

    T setExecutions(final List<E> executions);

    class JobTool {

        public static <T extends InheritableJob<T, P, L, E>,
                P extends Mergeable<P> & Properties,
                L extends Mergeable<L> & Listeners,
                E extends Inheritable & Execution>
        T inherit(final Class<T> clazz, final T _this, final InheritableJobLoader repository, final String defaultJobXml) {
            final T copy = _this.copy();
            if (copy.getParent() != null) {
                final T that = repository.findParent(clazz, copy, defaultJobXml);

                that.getExecutions().clear(); // 4.6.1.1

                BaseTool.inheritingElementRule(copy, that); // 4.6.1.2

                // 4.4
                copy.setProperties(Rules.merge(copy.getProperties(), that.getProperties()));
                copy.setListeners(Rules.merge(copy.getListeners(), that.getListeners()));
                // 4.1
                copy.setRestartable(Rules.attributeRule(copy.getRestartable(), that.getRestartable())); // 4.4.1
            }
            copy.setExecutions(Rules.inheritingList(repository, defaultJobXml, _this.getExecutions()));
            return copy;
        }

        public static <T extends InheritableJob<T, P, L, E>,
                P extends Mergeable<P> & Properties,
                L extends Mergeable<L> & Listeners,
                E extends Inheritable & Execution>
        T copy(final T _this, final T that) {
            BaseTool.copy(_this, that);
            that.setId(_this.getId());
            that.setRestartable(_this.getRestartable());
            that.setProperties(Rules.copy(_this.getProperties()));
            that.setListeners(Rules.copy(_this.getListeners()));
            that.setExecutions(Rules.copyList(_this.getExecutions()));
            return that;
        }
    }
}
