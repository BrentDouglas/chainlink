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
package io.machinecode.chainlink.core.jsl.impl.task;

import io.machinecode.chainlink.spi.jsl.task.ExceptionClass;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ExceptionClassImpl implements ExceptionClass, Serializable {
    private static final long serialVersionUID = 1L;

    private final String fqcn;
    private transient Class<?> clazz;

    public ExceptionClassImpl(final String fqcn) {
        this.fqcn = fqcn;
    }

    @Override
    public String getClassName() {
        return this.fqcn;
    }

    public boolean matches(final Class<?> theirs, final ClassLoader loader) throws ClassNotFoundException {
        return theirs.getCanonicalName().equals(getClassName())
                || this._load(loader).isAssignableFrom(theirs);
    }

    private Class<?> _load(final ClassLoader loader) throws ClassNotFoundException {
        if (clazz == null) {
            clazz = loader.loadClass(this.fqcn);
        }
        return clazz;
    }
}
