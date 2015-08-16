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
package io.machinecode.chainlink.test.cdi.producer;

import io.machinecode.chainlink.core.execution.artifact.listener.EventOrderListener;
import io.machinecode.chainlink.core.execution.artifact.listener.ExpectFailReadOpenExceptionEventOrderListener;
import io.machinecode.chainlink.core.execution.artifact.listener.ExpectFailWriteOpenExceptionEventOrderListener;
import io.machinecode.chainlink.core.execution.artifact.partition.TestCollector;
import io.machinecode.chainlink.core.execution.artifact.partition.FailTestCollector;
import io.machinecode.chainlink.core.execution.artifact.processor.AlwaysEventOrderProcessor;
import io.machinecode.chainlink.core.execution.artifact.processor.FailEventOrderProcessor;
import io.machinecode.chainlink.core.execution.artifact.processor.NeverEventOrderProcessor;
import io.machinecode.chainlink.core.execution.artifact.processor.OnceFailEventOrderProcessor;
import io.machinecode.chainlink.core.execution.artifact.reader.FailCheckpointAndCloseEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.reader.FailCheckpointEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.reader.FailCloseAlwaysEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.reader.FailCloseEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.reader.FailOpenAndCloseEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.reader.FailOpenEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.reader.FailReadAndCloseEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.reader.FailReadEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.reader.FailTwiceTwiceReadEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.reader.NeverEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.reader.OnceFailCheckpointEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.reader.OnceFailReadEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.reader.OneEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.reader.SixEventOrderReader;
import io.machinecode.chainlink.core.execution.artifact.writer.EventOrderWriter;
import io.machinecode.chainlink.core.execution.artifact.writer.FailCheckpointAndCloseEventOrderWriter;
import io.machinecode.chainlink.core.execution.artifact.writer.FailCheckpointEventOrderWriter;
import io.machinecode.chainlink.core.execution.artifact.writer.FailCloseEventOrderWriter;
import io.machinecode.chainlink.core.execution.artifact.writer.FailOpenAndCloseEventOrderWriter;
import io.machinecode.chainlink.core.execution.artifact.writer.FailOpenEventOrderWriter;
import io.machinecode.chainlink.core.execution.artifact.writer.FailWriteAndCloseEventOrderWriter;
import io.machinecode.chainlink.core.execution.artifact.writer.FailWriteEventOrderWriter;
import io.machinecode.chainlink.core.execution.artifact.writer.OnceFailCheckpointEventOrderWriter;
import io.machinecode.chainlink.core.execution.artifact.writer.OnceFailWriteEventOrderWriter;

import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChunkProducer {

    // Reader

    @Produces
    @Named("neverEventOrderReader")
    public NeverEventOrderReader neverEventOrderReader(@New NeverEventOrderReader that) {
        return that;
    }

    @Produces
    @Named("oneEventOrderReader")
    public OneEventOrderReader oneEventOrderReader(@New OneEventOrderReader that) {
        return that;
    }

    @Produces
    @Named("sixEventOrderReader")
    public SixEventOrderReader sixEventOrderReader(@New SixEventOrderReader that) {
        return that;
    }

    @Produces
    @Named("failCheckpointEventOrderReader")
    public FailCheckpointEventOrderReader failCheckpointEventOrderReader(@New FailCheckpointEventOrderReader that) {
        return that;
    }

    @Produces
    @Named("failCloseEventOrderReader")
    public FailCloseEventOrderReader failCloseEventOrderReader(@New FailCloseEventOrderReader that) {
        return that;
    }

    @Produces
    @Named("failCloseAlwaysEventOrderReader")
    public FailCloseAlwaysEventOrderReader failCloseAlwaysEventOrderReader(@New FailCloseAlwaysEventOrderReader that) {
        return that;
    }

    @Produces
    @Named("failOpenEventOrderReader")
    public FailOpenEventOrderReader failOpenEventOrderReader(@New FailOpenEventOrderReader that) {
        return that;
    }

    @Produces
    @Named("failOpenAndCloseEventOrderReader")
    public FailOpenAndCloseEventOrderReader failOpenAndCloseEventOrderReader(@New FailOpenAndCloseEventOrderReader that) {
        return that;
    }

    @Produces
    @Named("failReadEventOrderReader")
    public FailReadEventOrderReader failReadEventOrderReader(@New FailReadEventOrderReader that) {
        return that;
    }

    @Produces
    @Named("failReadAndCloseEventOrderReader")
    public FailReadAndCloseEventOrderReader failReadAndCloseEventOrderReader(@New FailReadAndCloseEventOrderReader that) {
        return that;
    }

    @Produces
    @Named("failCheckpointAndCloseEventOrderReader")
    public FailCheckpointAndCloseEventOrderReader failCheckpointAndCloseEventOrderReader(@New FailCheckpointAndCloseEventOrderReader that) {
        return that;
    }

    @Produces
    @Named("onceFailReadEventOrderReader")
    public OnceFailReadEventOrderReader onceFailReadEventOrderReader(@New OnceFailReadEventOrderReader that) {
        return that;
    }

    @Produces
    @Named("onceFailCheckpointEventOrderReader")
    public OnceFailCheckpointEventOrderReader onceFailCheckpointEventOrderReader(@New OnceFailCheckpointEventOrderReader that) {
        return that;
    }

    @Produces
    @Named("failTwiceTwiceReadEventOrderReader")
    public FailTwiceTwiceReadEventOrderReader failTwiceTwiceReadEventOrderReader(@New FailTwiceTwiceReadEventOrderReader that) {
        return that;
    }

    //Processor

    @Produces
    @Named("alwaysEventOrderProcessor")
    public AlwaysEventOrderProcessor alwaysEventOrderProcessor(@New AlwaysEventOrderProcessor that) {
        return that;
    }

    @Produces
    @Named("neverEventOrderProcessor")
    public NeverEventOrderProcessor neverEventOrderProcessor(@New NeverEventOrderProcessor that) {
        return that;
    }

    @Produces
    @Named("failEventOrderProcessor")
    public FailEventOrderProcessor failEventOrderProcessor(@New FailEventOrderProcessor that) {
        return that;
    }

    @Produces
    @Named("onceFailEventOrderProcessor")
    public OnceFailEventOrderProcessor onceFailEventOrderProcessor(@New OnceFailEventOrderProcessor that) {
        return that;
    }

    // Writer

    @Produces
    @Named("eventOrderWriter")
    public EventOrderWriter eventOrderWriter(@New EventOrderWriter that) {
        return that;
    }

    @Produces
    @Named("failCheckpointEventOrderWriter")
    public FailCheckpointEventOrderWriter failCheckpointEventOrderWriter(@New FailCheckpointEventOrderWriter that) {
        return that;
    }

    @Produces
    @Named("failCloseEventOrderWriter")
    public FailCloseEventOrderWriter failCloseEventOrderWriter(@New FailCloseEventOrderWriter that) {
        return that;
    }

    @Produces
    @Named("failOpenEventOrderWriter")
    public FailOpenEventOrderWriter failOpenEventOrderWriter(@New FailOpenEventOrderWriter that) {
        return that;
    }

    @Produces
    @Named("failOpenAndCloseEventOrderWriter")
    public FailOpenAndCloseEventOrderWriter failOpenAndCloseEventOrderWriter(@New FailOpenAndCloseEventOrderWriter that) {
        return that;
    }

    @Produces
    @Named("failWriteEventOrderWriter")
    public FailWriteEventOrderWriter failWriteEventOrderWriter(@New FailWriteEventOrderWriter that) {
        return that;
    }

    @Produces
    @Named("failWriteAndCloseEventOrderWriter")
    public FailWriteAndCloseEventOrderWriter failWriteAndCloseEventOrderWriter(@New FailWriteAndCloseEventOrderWriter that) {
        return that;
    }

    @Produces
    @Named("failCheckpointAndCloseEventOrderWriter")
    public FailCheckpointAndCloseEventOrderWriter failCheckpointAndCloseEventOrderWriter(@New FailCheckpointAndCloseEventOrderWriter that) {
        return that;
    }

    @Produces
    @Named("onceFailWriteEventOrderWriter")
    public OnceFailWriteEventOrderWriter onceFailWriteEventOrderWriter(@New OnceFailWriteEventOrderWriter that) {
        return that;
    }

    @Produces
    @Named("onceFailCheckpointEventOrderWriter")
    public OnceFailCheckpointEventOrderWriter onceFailCheckpointEventOrderWriter(@New OnceFailCheckpointEventOrderWriter that) {
        return that;
    }

    // Listeners

    @Produces
    @Named("eventOrderListener")
    public EventOrderListener eventOrderListener(@New EventOrderListener that) {
        return that;
    }

    @Produces
    @Named("expectFailReadOpenExceptionEventOrderListener")
    public ExpectFailReadOpenExceptionEventOrderListener expectFailReadExceptionEventOrderListener(@New ExpectFailReadOpenExceptionEventOrderListener that) {
        return that;
    }

    @Produces
    @Named("expectFailWriteOpenExceptionEventOrderListener")
    public ExpectFailWriteOpenExceptionEventOrderListener expectFailWriteExceptionEventOrderListener(@New ExpectFailWriteOpenExceptionEventOrderListener that) {
        return that;
    }

    @Produces @Named("failTripleTwiceEventOrderProcessor") public io.machinecode.chainlink.core.execution.artifact.processor.FailTripleTwiceEventOrderProcessor failTripleTwiceEventOrderProcessor(@New io.machinecode.chainlink.core.execution.artifact.processor.FailTripleTwiceEventOrderProcessor that) { return that; }
    @Produces @Named("failTripleTwiceReadEventOrderReader") public io.machinecode.chainlink.core.execution.artifact.reader.FailTripleTwiceReadEventOrderReader failTripleTwiceReadEventOrderReader(@New io.machinecode.chainlink.core.execution.artifact.reader.FailTripleTwiceReadEventOrderReader that) { return that; }
    @Produces @Named("stopEventOrderReader") public io.machinecode.chainlink.core.execution.artifact.reader.StopEventOrderReader stopEventOrderReader(@New io.machinecode.chainlink.core.execution.artifact.reader.StopEventOrderReader that) { return that; }
    @Produces @Named("failTripleTwiceEventOrderWriter") public io.machinecode.chainlink.core.execution.artifact.writer.FailTripleTwiceEventOrderWriter failTripleTwiceEventOrderWriter(@New io.machinecode.chainlink.core.execution.artifact.writer.FailTripleTwiceEventOrderWriter that) { return that; }

    @Produces @Named("errorEventOrderProcessor") public io.machinecode.chainlink.core.execution.artifact.processor.ErrorEventOrderProcessor errorEventOrderProcessor(@New io.machinecode.chainlink.core.execution.artifact.processor.ErrorEventOrderProcessor that) { return that; }
    @Produces @Named("errorCheckpointEventOrderReader") public io.machinecode.chainlink.core.execution.artifact.reader.ErrorCheckpointEventOrderReader errorCheckpointEventOrderReader(@New io.machinecode.chainlink.core.execution.artifact.reader.ErrorCheckpointEventOrderReader that) { return that; }
    @Produces @Named("errorCloseEventOrderReader") public io.machinecode.chainlink.core.execution.artifact.reader.ErrorCloseEventOrderReader errorCloseEventOrderReader(@New io.machinecode.chainlink.core.execution.artifact.reader.ErrorCloseEventOrderReader that) { return that; }
    @Produces @Named("errorOpenEventOrderReader") public io.machinecode.chainlink.core.execution.artifact.reader.ErrorOpenEventOrderReader errorOpenEventOrderReader(@New io.machinecode.chainlink.core.execution.artifact.reader.ErrorOpenEventOrderReader that) { return that; }
    @Produces @Named("errorReadEventOrderReader") public io.machinecode.chainlink.core.execution.artifact.reader.ErrorReadEventOrderReader errorReadEventOrderReader(@New io.machinecode.chainlink.core.execution.artifact.reader.ErrorReadEventOrderReader that) { return that; }
    @Produces @Named("errorCloseEventOrderWriter") public io.machinecode.chainlink.core.execution.artifact.writer.ErrorCloseEventOrderWriter errorCloseEventOrderWriter(@New io.machinecode.chainlink.core.execution.artifact.writer.ErrorCloseEventOrderWriter that) { return that; }
    @Produces @Named("errorOpenEventOrderWriter") public io.machinecode.chainlink.core.execution.artifact.writer.ErrorOpenEventOrderWriter errorOpenEventOrderWriter(@New io.machinecode.chainlink.core.execution.artifact.writer.ErrorOpenEventOrderWriter that) { return that; }
    @Produces @Named("errorWriteEventOrderWriter") public io.machinecode.chainlink.core.execution.artifact.writer.ErrorWriteEventOrderWriter errorWriteEventOrderWriter(@New io.machinecode.chainlink.core.execution.artifact.writer.ErrorWriteEventOrderWriter that) { return that; }
    @Produces @Named("errorCheckpointEventOrderWriter") public io.machinecode.chainlink.core.execution.artifact.writer.ErrorCheckpointEventOrderWriter errorCheckpointEventOrderWriter(@New io.machinecode.chainlink.core.execution.artifact.writer.ErrorCheckpointEventOrderWriter that) { return that; }

    @Produces @Named("failBeforeChunkListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailBeforeChunkListener failBeforeChunkListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailBeforeChunkListener that) { return that; }
    @Produces @Named("failOnErrorListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailOnErrorListener failOnErrorListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailOnErrorListener that) { return that; }
    @Produces @Named("failAfterChunkListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailAfterChunkListener failAfterChunkListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailAfterChunkListener that) { return that; }
    @Produces @Named("failBeforeReadListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailBeforeReadListener failBeforeReadListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailBeforeReadListener that) { return that; }
    @Produces @Named("failOnReadErrorListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailOnReadErrorListener failOnReadErrorListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailOnReadErrorListener that) { return that; }
    @Produces @Named("failAfterReadListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailAfterReadListener failAfterReadListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailAfterReadListener that) { return that; }
    @Produces @Named("failBeforeProcessListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailBeforeProcessListener failBeforeProcessListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailBeforeProcessListener that) { return that; }
    @Produces @Named("failOnProcessErrorListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailOnProcessErrorListener failOnProcessErrorListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailOnProcessErrorListener that) { return that; }
    @Produces @Named("failAfterProcessListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailAfterProcessListener failAfterProcessListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailAfterProcessListener that) { return that; }
    @Produces @Named("failBeforeWriteListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailBeforeWriteListener failBeforeWriteListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailBeforeWriteListener that) { return that; }
    @Produces @Named("failOnWriteErrorListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailOnWriteErrorListener failOnWriteErrorListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailOnWriteErrorListener that) { return that; }
    @Produces @Named("failAfterWriteListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailAfterWriteListener failAfterWriteListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailAfterWriteListener that) { return that; }

    @Produces @Named("failSkipReadListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailSkipReadListener failSkipReadListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailSkipReadListener that) { return that; }
    @Produces @Named("failSkipProcessListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailSkipProcessListener failSkipProcessListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailSkipProcessListener that) { return that; }
    @Produces @Named("failSkipWriteListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailSkipWriteListener failSkipWriteListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailSkipWriteListener that) { return that; }
    @Produces @Named("failRetryReadListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailRetryReadListener failRetryReadListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailRetryReadListener that) { return that; }
    @Produces @Named("failRetryProcessListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailRetryProcessListener failRetryProcessListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailRetryProcessListener that) { return that; }
    @Produces @Named("failRetryWriteListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailRetryWriteListener failRetryWriteListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailRetryWriteListener that) { return that; }

    @Produces @Named("failBeforeJobListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailBeforeJobListener failBeforeJobListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailBeforeJobListener that) { return that; }
    @Produces @Named("failAfterJobListener") public io.machinecode.chainlink.core.execution.artifact.listener.FailAfterJobListener failAfterJobListener(@New io.machinecode.chainlink.core.execution.artifact.listener.FailAfterJobListener that) { return that; }
    @Produces @Named("countListener") public io.machinecode.chainlink.core.execution.artifact.listener.CountListener countListener(@New io.machinecode.chainlink.core.execution.artifact.listener.CountListener that) { return that; }

    @Produces @Named("eventOrderCollector") public TestCollector eventOrderCollector(@New TestCollector that) { return that; }
    @Produces @Named("failEventOrderCollector") public FailTestCollector failEventOrderCollector(@New FailTestCollector that) { return that; }
}
