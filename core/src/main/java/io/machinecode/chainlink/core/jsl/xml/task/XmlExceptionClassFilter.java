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

import io.machinecode.chainlink.core.jsl.xml.XmlMergeableList;
import io.machinecode.chainlink.spi.jsl.inherit.task.InheritableExceptionClassFilter;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

import static io.machinecode.chainlink.spi.jsl.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
//@XmlType(name = "ExceptionClassFilter", propOrder = {
//        "includes",
//        "excludes"
//})
public class XmlExceptionClassFilter extends XmlMergeableList<XmlExceptionClassFilter> implements InheritableExceptionClassFilter<XmlExceptionClassFilter, XmlExceptionClass> {

    @XmlElement(name = "include", namespace = NAMESPACE, required = false)
    private List<XmlExceptionClass> includes;

    @XmlElement(name = "exclude", namespace = NAMESPACE, required = false)
    private List<XmlExceptionClass> excludes;


    public List<XmlExceptionClass> getIncludes() {
        return includes;
    }

    public XmlExceptionClassFilter setIncludes(final List<XmlExceptionClass> includes) {
        this.includes = includes;
        return this;
    }

    public List<XmlExceptionClass> getExcludes() {
        return excludes;
    }

    public XmlExceptionClassFilter setExcludes(final List<XmlExceptionClass> excludes) {
        this.excludes = excludes;
        return this;
    }

    @Override
    public XmlExceptionClassFilter copy() {
        return copy(new XmlExceptionClassFilter());
    }

    @Override
    public XmlExceptionClassFilter copy(final XmlExceptionClassFilter that) {
        return ExceptionClassFilterTool.copy(this, that);
        //that.setIncludes(Rules.copyList(this.includes));
        //that.addExcludes(Rules.copyList(this.excludes));
        //return that;
    }

    @Override
    public XmlExceptionClassFilter merge(final XmlExceptionClassFilter that) {
        return ExceptionClassFilterTool.merge(this, that);
        //if (this.merge) {
        //    this.includes = Rules.listRule(this.includes, that.includes);
        //    this.excludes = Rules.listRule(this.excludes, that.excludes);
        //}
        //return this;
    }
}
