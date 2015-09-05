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
package io.machinecode.chainlink.core.jsl.xml.execution;

import io.machinecode.chainlink.spi.jsl.inherit.execution.InheritableSplit;
import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.chainlink.spi.jsl.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Split", propOrder = {
//        "flows"
//})
public class XmlSplit implements XmlExecution<XmlSplit>, InheritableSplit<XmlSplit, XmlFlow> {

    @XmlID
    @XmlSchemaType(name = "ID")
    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "next", required = true)
    private String next;

    @XmlElement(name = "flow", namespace = NAMESPACE, required = false)
    private List<XmlFlow> flows = new ArrayList<XmlFlow>();


    @Override
    public String getId() {
        return id;
    }

    public XmlSplit setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getNext() {
        return next;
    }

    public XmlSplit setNext(final String next) {
        this.next = next;
        return this;
    }

    @Override
    public List<XmlFlow> getFlows() {
        return flows;
    }

    public XmlSplit setFlows(final List<XmlFlow> flows) {
        this.flows = flows;
        return this;
    }

    @Override
    public XmlSplit inherit(final InheritableJobLoader repository, final String defaultJobXml) {
        return SplitTool.inherit(this, repository, defaultJobXml);
    }

    @Override
    public XmlSplit copy() {
        return copy(new XmlSplit());
    }

    @Override
    public XmlSplit copy(final XmlSplit that) {
        return SplitTool.copy(this, that);
    }
}
