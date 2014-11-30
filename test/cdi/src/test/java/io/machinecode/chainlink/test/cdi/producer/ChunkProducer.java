package io.machinecode.chainlink.test.cdi.producer;

import io.machinecode.chainlink.test.core.execution.chunk.artifact.listener.ExpectFailReadOpenExceptionEventOrderListener;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.listener.ExpectFailWriteOpenExceptionEventOrderListener;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.processor.AlwaysEventOrderProcessor;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.processor.FailEventOrderProcessor;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.processor.OnceFailEventOrderProcessor;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.FailCheckpointAndCloseEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.FailCheckpointEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.FailCloseAlwaysEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.FailCloseEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.FailOpenAndCloseEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.FailOpenEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.FailReadAndCloseEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.FailReadEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.FailTwiceTwiceReadEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.OnceFailCheckpointEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.OnceFailReadEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.OneEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.listener.EventOrderListener;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.processor.NeverEventOrderProcessor;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.NeverEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.writer.EventOrderWriter;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.reader.SixEventOrderReader;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.writer.FailCheckpointAndCloseEventOrderWriter;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.writer.FailCheckpointEventOrderWriter;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.writer.FailCloseEventOrderWriter;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.writer.FailOpenAndCloseEventOrderWriter;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.writer.FailOpenEventOrderWriter;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.writer.FailWriteAndCloseEventOrderWriter;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.writer.FailWriteEventOrderWriter;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.writer.OnceFailCheckpointEventOrderWriter;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.writer.OnceFailWriteEventOrderWriter;

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
}
