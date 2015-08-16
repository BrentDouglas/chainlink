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
package io.machinecode.chainlink.spi.loader;

import io.machinecode.chainlink.spi.jsl.Job;

import javax.batch.operations.NoSuchJobException;

/**
 * <p>Provides {@link Job}'s to the Chainlink runtime.</p>
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JobLoader {

    /**
     * <p>Load a job from the named file (or equivalent).</p>
     *
     * @param jslName The name of the xml file (or equivalent) containing the job.
     * @return The {@link Job} contained within the reference to {@param jslName}.
     * @throws NoSuchJobException If this loader does not contain the specified job.
     */
    Job load(final String jslName) throws NoSuchJobException;
}
