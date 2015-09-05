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
package io.machinecode.chainlink.core.jsl.xml;

import io.machinecode.chainlink.spi.jsl.inherit.InheritableProperties;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.chainlink.spi.jsl.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Properties", propOrder = {
//        "properties"
//})
public class XmlProperties extends XmlMergeableList<XmlProperties> implements InheritableProperties<XmlProperties, XmlProperty> {

    @XmlAttribute(name = "partition", required = false)
    private String partition;

    @XmlElement(name = "property", namespace = NAMESPACE, required = false)
    private List<XmlProperty> properties = new ArrayList<XmlProperty>(0);

    @Override
    public String getPartition() {
        return partition;
    }

    public XmlProperties setPartition(final String partition) {
        this.partition = partition;
        return this;
    }

    @Override
    public List<XmlProperty> getProperties() {
        return properties;
    }

    public XmlProperties setProperties(final List<XmlProperty> properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public XmlProperties copy() {
        return copy(new XmlProperties());
    }

    @Override
    public XmlProperties copy(final XmlProperties that) {
        return PropertiesTool.copy(this, that);
    }

    @Override
    public XmlProperties merge(final XmlProperties that) {
        return PropertiesTool.merge(this, that);
    }
}
