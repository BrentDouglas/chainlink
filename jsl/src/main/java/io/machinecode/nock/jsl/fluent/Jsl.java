package io.machinecode.nock.jsl.fluent;

import io.machinecode.nock.jsl.fluent.execution.FluentDecision;
import io.machinecode.nock.jsl.fluent.execution.FluentFlow;
import io.machinecode.nock.jsl.fluent.execution.FluentSplit;
import io.machinecode.nock.jsl.fluent.execution.FluentStep;
import io.machinecode.nock.jsl.fluent.partition.FluentAnalyser;
import io.machinecode.nock.jsl.fluent.partition.FluentCollector;
import io.machinecode.nock.jsl.fluent.partition.FluentMapper;
import io.machinecode.nock.jsl.fluent.partition.FluentMapperPartition;
import io.machinecode.nock.jsl.fluent.partition.FluentPlan;
import io.machinecode.nock.jsl.fluent.partition.FluentPlanPartition;
import io.machinecode.nock.jsl.fluent.partition.FluentReducer;
import io.machinecode.nock.jsl.fluent.task.FluentBatchlet;
import io.machinecode.nock.jsl.fluent.task.FluentCheckpointAlgorithm;
import io.machinecode.nock.jsl.fluent.task.FluentChunk;
import io.machinecode.nock.jsl.fluent.task.FluentExceptionClass;
import io.machinecode.nock.jsl.fluent.task.FluentExceptionClassFilter;
import io.machinecode.nock.jsl.fluent.task.FluentItemProcessor;
import io.machinecode.nock.jsl.fluent.task.FluentItemReader;
import io.machinecode.nock.jsl.fluent.task.FluentItemWriter;
import io.machinecode.nock.jsl.fluent.transition.FluentEnd;
import io.machinecode.nock.jsl.fluent.transition.FluentFail;
import io.machinecode.nock.jsl.fluent.transition.FluentNext;
import io.machinecode.nock.jsl.fluent.transition.FluentStop;
import io.machinecode.nock.spi.element.partition.Mapper;
import io.machinecode.nock.spi.element.partition.Plan;
import io.machinecode.nock.spi.element.task.Batchlet;
import io.machinecode.nock.spi.element.task.Chunk;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Jsl {

    public static FluentJob job() {
        return new FluentJob();
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

    // Execution

    public static FluentDecision executionAsDecision() {
        return decision();
    }

    public static FluentFlow executionAsFlow() {
        return flow();
    }

    public static FluentSplit executionAsSplit() {
        return split();
    }

    public static FluentStep<Batchlet,Mapper> executionAsStepWithBatchletAndMapper() {
        return stepWithBatchletAndMapper();
    }

    public static FluentStep<Batchlet,Plan> executionAsStepWithBatchletAndPlan() {
        return stepWithBatchletAndPlan();
    }

    public static FluentStep<Chunk,Mapper> executionAsStepWithChunkAndMapper() {
        return stepWithChunkAndMapper();
    }

    public static FluentStep<Chunk,Plan> executionAsStepWithChunkAndPlan() {
        return stepWithChunkAndPlan();
    }

    public static FluentDecision decision() {
        return new FluentDecision();
    }

    public static FluentFlow flow() {
        return new FluentFlow();
    }

    public static FluentSplit split() {
        return new FluentSplit();
    }

    public static FluentStep<Batchlet,Mapper> stepWithBatchletAndMapper() {
        return new FluentStep<Batchlet,Mapper>();
    }

    public static FluentStep<Batchlet,Plan> stepWithBatchletAndPlan() {
        return new FluentStep<Batchlet,Plan>();
    }

    public static FluentStep<Chunk,Mapper> stepWithChunkAndMapper() {
        return new FluentStep<Chunk,Mapper>();
    }

    public static FluentStep<Chunk,Plan> stepWithChunkAndPlan() {
        return new FluentStep<Chunk,Plan>();
    }

    // Transition

    public static FluentEnd transitionAsEnd() {
        return end();
    }

    public static FluentFail transitionAsFail() {
        return fail();
    }

    public static FluentNext transitionAsNext() {
        return next();
    }

    public static FluentStop transitionAsStop() {
        return stop();
    }

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

    public static FluentPlanPartition partitionWithPlan() {
        return new FluentPlanPartition();
    }

    public static FluentMapperPartition partitionWithMapper() {
        return new FluentMapperPartition();
    }

    public static FluentMapper strategyAsMapper() {
        return mapper();
    }

    public static FluentPlan strategyAsPlan() {
        return plan();
    }

    public static FluentMapper mapper() {
        return new FluentMapper();
    }

    public static FluentPlan plan() {
        return new FluentPlan();
    }

    public static FluentReducer reducer() {
        return new FluentReducer();
    }

    // Task

    public static FluentBatchlet taskAsBatchlet() {
        return batchlet();
    }

    public static FluentChunk taskAsChunk() {
        return chunk();
    }

    public static FluentBatchlet batchlet() {
        return new FluentBatchlet();
    }

    public static FluentCheckpointAlgorithm checkpointAlgorithm() {
        return new FluentCheckpointAlgorithm();
    }

    public static FluentChunk chunk() {
        return new FluentChunk();
    }

    public static FluentExceptionClass classes() {
        return new FluentExceptionClass();
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
