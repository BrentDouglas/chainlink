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
package io.machinecode.chainlink.inject.guice;

import javax.batch.api.BatchProperty;
import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class BatchPropertyLiteral implements BatchProperty, Serializable {
    private static final long serialVersionUID = 1L;

    public static final BatchPropertyLiteral DEFAULT = new BatchPropertyLiteral("");

    private final String name;

    public BatchPropertyLiteral(final String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return BatchProperty.class;
    }

    @Override
    public int hashCode() {
        return (127 * "name".hashCode()) ^ name.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof BatchProperty && name.equals(((BatchProperty) o).name());
    }
}
