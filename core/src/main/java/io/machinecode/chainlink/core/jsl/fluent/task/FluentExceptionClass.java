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
package io.machinecode.chainlink.core.jsl.fluent.task;

import io.machinecode.chainlink.spi.jsl.inherit.task.InheritableExceptionClass;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentExceptionClass implements InheritableExceptionClass<FluentExceptionClass> {

    private String className;

    @Override
    public String getClassName() {
        return this.className;
    }

    public FluentExceptionClass setClassName(final String className) {
        this.className = className;
        return this;
    }

    @Override
    public FluentExceptionClass copy() {
        return copy(new FluentExceptionClass());
    }

    @Override
    public FluentExceptionClass copy(final FluentExceptionClass that) {
        return ExceptionClassTool.copy(this, that);
    }

    @Override
    public FluentExceptionClass merge(final FluentExceptionClass that) {
        return ExceptionClassTool.merge(this, that);
    }
}
