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
package io.machinecode.chainlink.core.jsl.xml.transition;

import io.machinecode.chainlink.spi.jsl.inherit.transition.InheritableStop;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(FIELD)
//@XmlType(name = "Stop")
public class XmlStop extends XmlTerminatingTransition<XmlStop> implements XmlTransition<XmlStop>, InheritableStop<XmlStop> {

    @XmlAttribute(name = "restart", required = false)
    private String restart;


    @Override
    public String getRestart() {
        return restart;
    }

    public XmlStop setRestart(final String restart) {
        this.restart = restart;
        return this;
    }

    @Override
    public XmlStop copy() {
        return copy(new XmlStop());
    }

    @Override
    public XmlStop copy(final XmlStop that) {
        return StopTool.copy(this, that);
        //that.setRestart(this.restart);
        //return super.copy(that);
    }
}
