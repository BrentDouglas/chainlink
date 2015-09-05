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

import io.machinecode.chainlink.spi.jsl.transition.Next;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(FIELD)
//@XmlType(name = "Next")
public class XmlNext implements XmlTransition<XmlNext>, Next {

    @XmlAttribute(name = "on", required = true)
    private String on;

    @XmlAttribute(name = "to", required = true)
    private String to;


    @Override
    public String getOn() {
        return on;
    }

    public XmlNext setOn(final String on) {
        this.on = on;
        return this;
    }

    @Override
    public String getTo() {
        return to;
    }

    public XmlNext setTo(final String to) {
        this.to = to;
        return this;
    }

    @Override
    public XmlNext copy() {
        return copy(new XmlNext());
    }

    @Override
    public XmlNext copy(final XmlNext that) {
        that.setOn(this.on);
        that.setTo(this.to);
        return that;
    }
}
