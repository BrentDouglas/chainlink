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

import io.machinecode.chainlink.spi.jsl.inherit.task.InheritableChunk;
import io.machinecode.chainlink.spi.jsl.task.Chunk;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import static io.machinecode.chainlink.spi.jsl.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Chunk", propOrder = {
//        "reader",
//        "processor",
//        "writer",
//        "checkpointAlgorithm",
//        "skippableExceptionClasses",
//        "retryableExceptionClasses",
//        "noRollbackExceptionClasses"
//})
public class XmlChunk implements XmlTask<XmlChunk>, InheritableChunk<XmlChunk, XmlItemReader, XmlItemProcessor, XmlItemWriter, XmlCheckpointAlgorithm, XmlExceptionClassFilter>, Chunk {

    @XmlAttribute(name = "checkpoint-policy", required = false)
    private String checkpointPolicy = CheckpointPolicy.ITEM;

    @XmlAttribute(name = "item-count", required = false)
    private String itemCount = TEN;

    @XmlAttribute(name = "time-limit", required = false)
    private String timeLimit = ZERO;

    @XmlAttribute(name = "skip-limit", required = false)
    private String skipLimit = MINUS_ONE;

    @XmlAttribute(name = "retry-limit", required = false)
    private String retryLimit = MINUS_ONE;

    @XmlElement(name = "reader", namespace = NAMESPACE, required = true)
    private XmlItemReader reader;

    @XmlElement(name = "processor", namespace = NAMESPACE, required = false)
    private XmlItemProcessor processor;

    @XmlElement(name = "writer", namespace = NAMESPACE, required = true)
    private XmlItemWriter writer;

    @XmlElement(name = "checkpoint-algorithm", namespace = NAMESPACE, required = false)
    private XmlCheckpointAlgorithm checkpointAlgorithm;

    @XmlElement(name = "skippable-exception-classes", namespace = NAMESPACE, required = false)
    private XmlExceptionClassFilter skippableExceptionClasses;

    @XmlElement(name = "retryable-exception-classes", namespace = NAMESPACE, required = false)
    private XmlExceptionClassFilter retryableExceptionClasses;

    @XmlElement(name = "no-rollback-exception-classes", namespace = NAMESPACE, required = false)
    private XmlExceptionClassFilter noRollbackExceptionClasses;


    public String getCheckpointPolicy() {
        return checkpointPolicy;
    }

    public XmlChunk setCheckpointPolicy(final String checkpointPolicy) {
        this.checkpointPolicy = checkpointPolicy;
        return this;
    }

    public String getItemCount() {
        return itemCount;
    }

    public XmlChunk setItemCount(final String itemCount) {
        this.itemCount = itemCount;
        return this;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public XmlChunk setTimeLimit(final String timeLimit) {
        this.timeLimit = timeLimit;
        return this;
    }

    public String getSkipLimit() {
        return skipLimit;
    }

    public XmlChunk setSkipLimit(final String skipLimit) {
        this.skipLimit = skipLimit;
        return this;
    }

    public String getRetryLimit() {
        return retryLimit;
    }

    public XmlChunk setRetryLimit(final String retryLimit) {
        this.retryLimit = retryLimit;
        return this;
    }

    public XmlItemReader getReader() {
        return reader;
    }

    public XmlChunk setReader(final XmlItemReader reader) {
        this.reader = reader;
        return this;
    }

    public XmlItemProcessor getProcessor() {
        return processor;
    }

    public XmlChunk setProcessor(final XmlItemProcessor processor) {
        this.processor = processor;
        return this;
    }

    public XmlItemWriter getWriter() {
        return writer;
    }

    public XmlChunk setWriter(final XmlItemWriter writer) {
        this.writer = writer;
        return this;
    }

    public XmlCheckpointAlgorithm getCheckpointAlgorithm() {
        return checkpointAlgorithm;
    }

    public XmlChunk setCheckpointAlgorithm(final XmlCheckpointAlgorithm checkpointAlgorithm) {
        this.checkpointAlgorithm = checkpointAlgorithm;
        return this;
    }

    public XmlExceptionClassFilter getSkippableExceptionClasses() {
        return skippableExceptionClasses;
    }

    public XmlChunk setSkippableExceptionClasses(final XmlExceptionClassFilter skippableExceptionClasses) {
        this.skippableExceptionClasses = skippableExceptionClasses;
        return this;
    }

    public XmlExceptionClassFilter getRetryableExceptionClasses() {
        return retryableExceptionClasses;
    }

    public XmlChunk setRetryableExceptionClasses(final XmlExceptionClassFilter retryableExceptionClasses) {
        this.retryableExceptionClasses = retryableExceptionClasses;
        return this;
    }

    public XmlExceptionClassFilter getNoRollbackExceptionClasses() {
        return noRollbackExceptionClasses;
    }

    public XmlChunk setNoRollbackExceptionClasses(final XmlExceptionClassFilter noRollbackExceptionClasses) {
        this.noRollbackExceptionClasses = noRollbackExceptionClasses;
        return this;
    }

    @Override
    public XmlChunk copy() {
        return copy(new XmlChunk());
    }

    @Override
    public XmlChunk copy(final XmlChunk that) {
        return ChunkTool.copy(this, that);
        //that.setCheckpointPolicy(this.checkpointPolicy);
        //that.setItemCount(this.itemCount);
        //that.setTimeLimit(this.timeLimit);
        //that.setSkipLimit(this.skipLimit);
        //that.setRetryLimit(this.retryLimit);
        //that.setReader(Rules.copy(this.reader));
        //that.setProcessor(Rules.copy(this.processor));
        //that.setWriter(Rules.copy(this.writer));
        //that.setCheckpointAlgorithm(Rules.copy(this.checkpointAlgorithm));
        //that.setSkippableExceptionClasses(Rules.copy(this.skippableExceptionClasses));
        //that.setRetryableExceptionClasses(Rules.copy(this.retryableExceptionClasses));
        //that.setNoRollbackExceptionClasses(Rules.copy(this.noRollbackExceptionClasses));
        //return that;
    }

    @Override
    public XmlChunk merge(final XmlChunk that) {
        return ChunkTool.merge(this, that);
        //this.setCheckpointPolicy(Rules.attributeRule(this.checkpointPolicy, that.checkpointPolicy));
        //this.setItemCount(Rules.attributeRule(this.itemCount, that.itemCount));
        //this.setTimeLimit(Rules.attributeRule(this.timeLimit, that.timeLimit));
        //this.setSkipLimit(Rules.attributeRule(this.skipLimit, that.skipLimit));
        //this.setRetryLimit(Rules.attributeRule(this.retryLimit, that.retryLimit));
        //this.setReader(Rules.merge(this.reader, that.reader));
        //this.setProcessor(Rules.merge(this.processor, that.processor));
        //this.setWriter(Rules.merge(this.writer, that.writer));
        //this.setCheckpointAlgorithm(Rules.merge(this.checkpointAlgorithm, that.checkpointAlgorithm));
        //this.setSkippableExceptionClasses(Rules.merge(this.skippableExceptionClasses, that.skippableExceptionClasses));
        //this.setRetryableExceptionClasses(Rules.merge(this.retryableExceptionClasses, that.retryableExceptionClasses));
        //this.setNoRollbackExceptionClasses(Rules.merge(this.noRollbackExceptionClasses, that.noRollbackExceptionClasses));
        //return this;
    }
}
