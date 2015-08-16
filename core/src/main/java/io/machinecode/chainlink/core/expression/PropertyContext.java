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
package io.machinecode.chainlink.core.expression;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.inject.ArtifactReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class PropertyContext {

    private final TMap<String,ArtifactReference> references = new THashMap<>();
    final PropertyResolver properties;

    protected PropertyContext(final PropertyResolver properties) {
        this.properties = properties;
    }

    //TODO This doesn't really belong in the Expression package
    public ArtifactReference getReference(final ArtifactReference that) {
        final ArtifactReference old = references.putIfAbsent(that.ref(), that);
        return old == null ? that : old;
    }
}
