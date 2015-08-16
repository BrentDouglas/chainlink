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
package io.machinecode.chainlink.core.jsl.xml.task;

import io.machinecode.chainlink.spi.jsl.inherit.task.InheritableExceptionClass;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public class XmlExceptionClass implements InheritableExceptionClass<XmlExceptionClass> {

    @XmlAttribute(name = "class", required = true)
    private String className;

    @Override
    public String getClassName() {
        return className;
    }

    public XmlExceptionClass setClassName(final String className) {
        this.className = className;
        return this;
    }

    @Override
    public XmlExceptionClass copy() {
        return copy(new XmlExceptionClass());
    }

    @Override
    public XmlExceptionClass copy(final XmlExceptionClass that) {
        return ExceptionClassTool.copy(this, that);
        //that.setClassName(this.className);
        //return that;
    }

    @Override
    public XmlExceptionClass merge(final XmlExceptionClass that) {
        return ExceptionClassTool.merge(this, that);
        //this.setClassName(Rules.attributeRule(this.className, that.className));
        //return this;
    }
}
