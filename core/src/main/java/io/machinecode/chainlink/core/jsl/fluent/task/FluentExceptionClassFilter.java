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
package io.machinecode.chainlink.core.jsl.fluent.task;

import io.machinecode.chainlink.core.jsl.fluent.FluentMergeableList;
import io.machinecode.chainlink.spi.jsl.inherit.task.InheritableExceptionClassFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentExceptionClassFilter
        extends FluentMergeableList<FluentExceptionClassFilter>
        implements InheritableExceptionClassFilter<FluentExceptionClassFilter, FluentExceptionClass> {

    private List<FluentExceptionClass> includes = new ArrayList<>(0);
    private List<FluentExceptionClass> excludes = new ArrayList<>(0);

    @Override
    public List<FluentExceptionClass> getIncludes() {
        return this.includes;
    }

    @Override
    public FluentExceptionClassFilter setIncludes(final List<FluentExceptionClass> includes) {
        this.includes = includes;
        return this;
    }

    public FluentExceptionClassFilter addIncludes(final String... includes) {
        for (final String include : includes) {
            this.includes.add(new FluentExceptionClass().setClassName(include));
        }
        return this;
    }

    public FluentExceptionClassFilter addIncludes(final Class<? extends Throwable>... includes) {
        for (final Class<? extends Throwable> include : includes) {
            this.includes.add(new FluentExceptionClass().setClassName(include.getCanonicalName()));
        }
        return this;
    }

    public FluentExceptionClassFilter addInclude(final String include) {
        this.includes.add(new FluentExceptionClass().setClassName(include));
        return this;
    }

    public FluentExceptionClassFilter addInclude(final Class<? extends Throwable> include) {
        this.includes.add(new FluentExceptionClass().setClassName(include.getCanonicalName()));
        return this;
    }

    @Override
    public List<FluentExceptionClass> getExcludes() {
        return this.excludes;
    }

    @Override
    public FluentExceptionClassFilter setExcludes(final List<FluentExceptionClass> excludes) {
        this.excludes = excludes;
        return this;
    }

    public FluentExceptionClassFilter addExcludes(final String... excludes) {
        for (final String exclude : excludes) {
            this.excludes.add(new FluentExceptionClass().setClassName(exclude));
        }
        return this;
    }

    public FluentExceptionClassFilter addExcludes(final Class<? extends Throwable>... excludes) {
        for (final Class<? extends Throwable> exclude : excludes) {
            this.excludes.add(new FluentExceptionClass().setClassName(exclude.getCanonicalName()));
        }
        return this;
    }

    public FluentExceptionClassFilter addExclude(final String exclude) {
        this.excludes.add(new FluentExceptionClass().setClassName(exclude));
        return this;
    }

    public FluentExceptionClassFilter addExclude(final Class<? extends Throwable> exclude) {
        this.excludes.add(new FluentExceptionClass().setClassName(exclude.getCanonicalName()));
        return this;
    }

    @Override
    public FluentExceptionClassFilter copy() {
        return copy(new FluentExceptionClassFilter());
    }

    @Override
    public FluentExceptionClassFilter copy(final FluentExceptionClassFilter that) {
        return ExceptionClassFilterTool.copy(this, that);
    }

    @Override
    public FluentExceptionClassFilter merge(final FluentExceptionClassFilter that) {
        return ExceptionClassFilterTool.merge(this, that);
    }
}
