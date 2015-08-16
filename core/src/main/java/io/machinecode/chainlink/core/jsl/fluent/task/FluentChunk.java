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
package io.machinecode.chainlink.core.jsl.fluent.task;

import io.machinecode.chainlink.spi.jsl.inherit.task.InheritableChunk;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentChunk implements FluentTask<FluentChunk>, InheritableChunk<FluentChunk, FluentItemReader, FluentItemProcessor, FluentItemWriter, FluentCheckpointAlgorithm, FluentExceptionClassFilter> {

    private String checkpointPolicy = CheckpointPolicy.ITEM;
    private String itemCount = TEN;
    private String timeLimit = ZERO;
    private String skipLimit = MINUS_ONE;
    private String retryLimit = MINUS_ONE;
    private FluentItemReader reader;
    private FluentItemProcessor processor;
    private FluentItemWriter writer;
    private FluentCheckpointAlgorithm checkpointAlgorithm;
    private FluentExceptionClassFilter skippableExceptionClasses;
    private FluentExceptionClassFilter retryableExceptionClasses;
    private FluentExceptionClassFilter noRollbackExceptionClasses;


    @Override
    public String getCheckpointPolicy() {
        return this.checkpointPolicy;
    }

    public FluentChunk setCheckpointPolicy(final String checkpointPolicy) {
        this.checkpointPolicy = checkpointPolicy;
        return this;
    }

    @Override
    public String getItemCount() {
        return this.itemCount;
    }

    public FluentChunk setItemCount(final String itemCount) {
        this.itemCount = itemCount;
        return this;
    }

    @Override
    public String getTimeLimit() {
        return this.timeLimit;
    }

    public FluentChunk setTimeLimit(final String timeLimit) {
        this.timeLimit = timeLimit;
        return this;
    }

    @Override
    public String getSkipLimit() {
        return this.skipLimit;
    }

    public FluentChunk setSkipLimit(final String skipLimit) {
        this.skipLimit = skipLimit;
        return this;
    }

    @Override
    public String getRetryLimit() {
        return this.retryLimit;
    }

    public FluentChunk setRetryLimit(final String retryLimit) {
        this.retryLimit = retryLimit;
        return this;
    }

    @Override
    public FluentItemReader getReader() {
        return this.reader;
    }

    public FluentChunk setReader(final FluentItemReader reader) {
        this.reader = reader;
        return this;
    }

    @Override
    public FluentItemProcessor getProcessor() {
        return this.processor;
    }

    public FluentChunk setProcessor(final FluentItemProcessor processor) {
        this.processor = processor;
        return this;
    }

    @Override
    public FluentItemWriter getWriter() {
        return this.writer;
    }

    public FluentChunk setWriter(final FluentItemWriter writer) {
        this.writer = writer;
        return this;
    }

    @Override
    public FluentCheckpointAlgorithm getCheckpointAlgorithm() {
        return this.checkpointAlgorithm;
    }

    public FluentChunk setCheckpointAlgorithm(final FluentCheckpointAlgorithm checkpointAlgorithm) {
        this.checkpointAlgorithm = checkpointAlgorithm;
        return this;
    }

    @Override
    public FluentExceptionClassFilter getSkippableExceptionClasses() {
        return this.skippableExceptionClasses;
    }

    public FluentChunk setSkippableExceptionClasses(final FluentExceptionClassFilter skippableExceptionClasses) {
        this.skippableExceptionClasses = skippableExceptionClasses;
        return this;
    }

    @Override
    public FluentExceptionClassFilter getRetryableExceptionClasses() {
        return this.retryableExceptionClasses;
    }

    public FluentChunk setRetryableExceptionClasses(final FluentExceptionClassFilter retryableExceptionClasses) {
        this.retryableExceptionClasses = retryableExceptionClasses;
        return this;
    }

    @Override
    public FluentExceptionClassFilter getNoRollbackExceptionClasses() {
        return this.noRollbackExceptionClasses;
    }

    public FluentChunk setNoRollbackExceptionClasses(final FluentExceptionClassFilter noRollbackExceptionClasses) {
        this.noRollbackExceptionClasses = noRollbackExceptionClasses;
        return this;
    }

    @Override
    public FluentChunk copy() {
        return copy(new FluentChunk());
    }

    @Override
    public FluentChunk copy(final FluentChunk that) {
        return ChunkTool.copy(this, that);
    }

    @Override
    public FluentChunk merge(final FluentChunk that) {
        return ChunkTool.merge(this, that);
    }
}
