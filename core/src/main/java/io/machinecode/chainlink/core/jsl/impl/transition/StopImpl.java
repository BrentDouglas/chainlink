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

import io.machinecode.chainlink.spi.jsl.transition.Stop;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class StopImpl extends TransitionImpl implements Stop {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(StopImpl.class);

    private final String exitStatus;
    private final String restart;

    public StopImpl(final String on, final String exitStatus, final String restart) {
        super(on);
        this.exitStatus = exitStatus;
        this.restart = restart;
    }

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }

    @Override
    public String getRestart() {
        return this.restart;
    }

    @Override
    public String element() {
        return Stop.ELEMENT;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return BatchStatus.STOPPING;
    }

    @Override
    public String getNext() {
        return null;
    }

    @Override
    public String getRestartId() {
        return this.restart;
    }

    @Override
    public boolean isTerminating() {
        return true;
    }
}
