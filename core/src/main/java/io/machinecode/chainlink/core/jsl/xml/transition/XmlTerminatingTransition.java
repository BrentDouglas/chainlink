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
package io.machinecode.chainlink.core.jsl.xml.transition;

import io.machinecode.chainlink.spi.jsl.inherit.transition.InheritableTerminatingTransition;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public abstract class XmlTerminatingTransition<T extends XmlTerminatingTransition<T>> implements InheritableTerminatingTransition<T> {

    @XmlAttribute(name = "on", required = true)
    private String on;

    @XmlAttribute(name = "exit-status", required = false)
    private String exitStatus;

    public String getOn() {
        return on;
    }

    @SuppressWarnings("unchecked")
    public T setOn(final String on) {
        this.on = on;
        return (T)this;
    }

    public String getExitStatus() {
        return exitStatus;
    }

    @SuppressWarnings("unchecked")
    public T setExitStatus(final String exitStatus) {
        this.exitStatus = exitStatus;
        return (T)this;
    }

    @Override
    public T copy(final T that) {
        return TerminatingTransitionTool.copy((T)this, that);
    }
}
