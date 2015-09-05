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
package io.machinecode.chainlink.core.jsl.impl.transition;

import io.machinecode.chainlink.spi.jsl.transition.Transition;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class TransitionImpl implements Transition, Serializable {
    private static final long serialVersionUID = 1L;

    private final String on;

    public TransitionImpl(final String on) {
        this.on = on;
    }

    @Override
    public String getOn() {
        return this.on;
    }

    public abstract String getExitStatus();

    public abstract String element();

    public abstract BatchStatus getBatchStatus();

    public abstract String getNext();

    public abstract String getRestartId();

    public abstract boolean isTerminating();
}
