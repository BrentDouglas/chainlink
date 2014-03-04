package io.machinecode.chainlink.jsl.xml.task;

import io.machinecode.chainlink.jsl.inherit.task.InheritableChunk;
import io.machinecode.chainlink.spi.element.task.Chunk;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import static io.machinecode.chainlink.spi.element.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
        //that.setReader(Util.copy(this.reader));
        //that.setProcessor(Util.copy(this.processor));
        //that.setWriter(Util.copy(this.writer));
        //that.setCheckpointAlgorithm(Util.copy(this.checkpointAlgorithm));
        //that.setSkippableExceptionClasses(Util.copy(this.skippableExceptionClasses));
        //that.setRetryableExceptionClasses(Util.copy(this.retryableExceptionClasses));
        //that.setNoRollbackExceptionClasses(Util.copy(this.noRollbackExceptionClasses));
        //return that;
    }

    @Override
    public XmlChunk merge(final XmlChunk that) {
        return ChunkTool.merge(this, that);
        //this.setCheckpointPolicy(Util.attributeRule(this.checkpointPolicy, that.checkpointPolicy));
        //this.setItemCount(Util.attributeRule(this.itemCount, that.itemCount));
        //this.setTimeLimit(Util.attributeRule(this.timeLimit, that.timeLimit));
        //this.setSkipLimit(Util.attributeRule(this.skipLimit, that.skipLimit));
        //this.setRetryLimit(Util.attributeRule(this.retryLimit, that.retryLimit));
        //this.setReader(Util.merge(this.reader, that.reader));
        //this.setProcessor(Util.merge(this.processor, that.processor));
        //this.setWriter(Util.merge(this.writer, that.writer));
        //this.setCheckpointAlgorithm(Util.merge(this.checkpointAlgorithm, that.checkpointAlgorithm));
        //this.setSkippableExceptionClasses(Util.merge(this.skippableExceptionClasses, that.skippableExceptionClasses));
        //this.setRetryableExceptionClasses(Util.merge(this.retryableExceptionClasses, that.retryableExceptionClasses));
        //this.setNoRollbackExceptionClasses(Util.merge(this.noRollbackExceptionClasses, that.noRollbackExceptionClasses));
        //return this;
    }
}
