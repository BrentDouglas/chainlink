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
package io.machinecode.chainlink.spi.inject;

import io.machinecode.chainlink.spi.context.ExecutionContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ArtifactReference {

    /**
     * @return The identifier of this artifact.
     */
    String ref();

    /**
     * @param as The interface to load the artifact as.
     * @param injectionContext
     * @param context
     * @param <T> The type of the loaded artifact.
     * @return The artifact identified by {@link #ref()} or null if none can be found.
     * @throws ArtifactOfWrongTypeException If the artifact loaded does not implement {@param as}.
     * @throws Exception If an error occurs.
     */
    <T> T load(final Class<T> as, final InjectionContext injectionContext, final ExecutionContext context) throws Exception;
}
