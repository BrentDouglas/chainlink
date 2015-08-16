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
package io.machinecode.chainlink.core.jsl.xml;

import io.machinecode.chainlink.spi.jsl.inherit.InheritableProperty;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Property")
public class XmlProperty implements InheritableProperty<XmlProperty> {

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "value", required = true)
    private String value;


    @Override
    public String getName() {
        return name;
    }

    public XmlProperty setName(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getValue() {
        return value;
    }

    public XmlProperty setValue(final String value) {
        this.value = value;
        return this;
    }

    @Override
    public XmlProperty copy() {
        return copy(new XmlProperty());
    }

    @Override
    public XmlProperty copy(final XmlProperty that) {
        return PropertyTool.copy(this, that);
    }
}
