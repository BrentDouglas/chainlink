package io.machinecode.nock.jsl.fluent;

import io.machinecode.nock.jsl.fluent.chunk.FluentCheckpointAlgorithm;
import io.machinecode.nock.jsl.fluent.chunk.FluentChunk;
import io.machinecode.nock.jsl.fluent.chunk.FluentClasses;
import io.machinecode.nock.jsl.fluent.chunk.FluentExceptionClassFilter;
import io.machinecode.nock.jsl.fluent.chunk.FluentItemProcessor;
import io.machinecode.nock.jsl.fluent.chunk.FluentItemReader;
import io.machinecode.nock.jsl.fluent.chunk.FluentItemWriter;
import io.machinecode.nock.jsl.fluent.partition.FluentAnalyser;
import io.machinecode.nock.jsl.fluent.partition.FluentCollector;
import io.machinecode.nock.jsl.fluent.partition.FluentMapperPartition;
import io.machinecode.nock.jsl.fluent.partition.FluentPartitionMapper;
import io.machinecode.nock.jsl.fluent.partition.FluentPartitionPlan;
import io.machinecode.nock.jsl.fluent.partition.FluentPartitionReducer;
import io.machinecode.nock.jsl.fluent.partition.FluentPlanPartition;
import io.machinecode.nock.jsl.fluent.transition.FluentEnd;
import io.machinecode.nock.jsl.fluent.transition.FluentFail;
import io.machinecode.nock.jsl.fluent.transition.FluentNext;
import io.machinecode.nock.jsl.fluent.transition.FluentStop;
import io.machinecode.nock.jsl.fluent.type.FluentBatchletMapperStep;
import io.machinecode.nock.jsl.fluent.type.FluentBatchletPlanStep;
import io.machinecode.nock.jsl.fluent.type.FluentChunkMapperStep;
import io.machinecode.nock.jsl.fluent.type.FluentChunkPlanStep;
import io.machinecode.nock.jsl.fluent.type.FluentDecision;
import io.machinecode.nock.jsl.fluent.type.FluentFlow;
import io.machinecode.nock.jsl.fluent.type.FluentSplit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Fluent {

    public static FluentJob job() {
        return new FluentJob();
    }

    public static FluentBatchlet batchlet() {
        return new FluentBatchlet();
    }

    public static FluentListener listener() {
        return new FluentListener();
    }

    public static FluentListeners listeners() {
        return new FluentListeners();
    }

    public static FluentProperty property() {
        return new FluentProperty();
    }

    public static FluentProperties properties() {
        return new FluentProperties();
    }

    // Transition

    public static FluentDecision decision() {
        return new FluentDecision();
    }

    public static FluentFlow flow() {
        return new FluentFlow();
    }

    public static FluentSplit split() {
        return new FluentSplit();
    }

    public static FluentBatchletMapperStep batchletMapperStep() {
        return new FluentBatchletMapperStep();
    }

    public static FluentBatchletPlanStep batchletPlanStep() {
        return new FluentBatchletPlanStep();
    }

    public static FluentChunkMapperStep chunkMapperStep() {
        return new FluentChunkMapperStep();
    }

    public static FluentChunkPlanStep chunkPlanStep() {
        return new FluentChunkPlanStep();
    }

    // Type

    public static FluentEnd end() {
        return new FluentEnd();
    }

    public static FluentFail fail() {
        return new FluentFail();
    }

    public static FluentNext next() {
        return new FluentNext();
    }

    public static FluentStop stop() {
        return new FluentStop();
    }

    // Partition

    public static FluentAnalyser analyser() {
        return new FluentAnalyser();
    }

    public static FluentCollector collector() {
        return new FluentCollector();
    }

    public static FluentPlanPartition planPartition() {
        return new FluentPlanPartition();
    }

    public static FluentMapperPartition mapperPartition() {
        return new FluentMapperPartition();
    }

    public static FluentPartitionMapper mapper() {
        return new FluentPartitionMapper();
    }

    public static FluentPartitionPlan plan() {
        return new FluentPartitionPlan();
    }

    public static FluentPartitionReducer reducer() {
        return new FluentPartitionReducer();
    }

    // Chunk

    public static FluentCheckpointAlgorithm checkpointAlgorithm() {
        return new FluentCheckpointAlgorithm();
    }

    public static FluentChunk chunk() {
        return new FluentChunk();
    }

    public static FluentClasses classes() {
        return new FluentClasses();
    }

    public static FluentExceptionClassFilter filter() {
        return new FluentExceptionClassFilter();
    }

    public static FluentItemProcessor processor() {
        return new FluentItemProcessor();
    }

    public static FluentItemReader reader() {
        return new FluentItemReader();
    }

    public static FluentItemWriter writer() {
        return new FluentItemWriter();
    }
}
