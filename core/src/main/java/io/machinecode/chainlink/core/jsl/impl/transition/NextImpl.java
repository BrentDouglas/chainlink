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

import io.machinecode.chainlink.spi.jsl.transition.Next;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NextImpl extends TransitionImpl implements Next {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(NextImpl.class);

    private final String to;

    public NextImpl(final String on, final String to) {
        super(on);
        this.to = to;
    }

    @Override
    public String getTo() {
        return this.to;
    }

    @Override
    public String element() {
        return Next.ELEMENT;
    }

    @Override
    public String getExitStatus() {
        return null;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return null;
    }

    @Override
    public String getNext() {
        return this.to;
    }

    @Override
    public String getRestartId() {
        return null;
    }

    @Override
    public boolean isTerminating() {
        return false;
    }
}
