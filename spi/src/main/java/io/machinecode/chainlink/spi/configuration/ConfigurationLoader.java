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
package io.machinecode.chainlink.spi.configuration;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ConfigurationLoader {

    /**
     * @param id The identifier of the artifact to load.
     * @param as An interface the artifact is expected to implement.
     * @param loader The configured classloader.
     * @param <T> The required type of the artifact.
     * @return An artifact matching {@param id} or null if no artifact matching is able to be loaded.
     * @throws io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException If the artifact was able to be loaded but does not implement {@param as}
     * @throws Exception On implementation specific issues.
     */
    <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception;
}
