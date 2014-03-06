package io.machinecode.chainlink.jsl.fluent;

import io.machinecode.chainlink.jsl.fluent.execution.FluentDecision;
import io.machinecode.chainlink.jsl.fluent.execution.FluentFlow;
import io.machinecode.chainlink.jsl.fluent.execution.FluentSplit;
import io.machinecode.chainlink.jsl.fluent.execution.FluentStep;
import io.machinecode.chainlink.jsl.fluent.partition.FluentAnalyser;
import io.machinecode.chainlink.jsl.fluent.partition.FluentCollector;
import io.machinecode.chainlink.jsl.fluent.partition.FluentMapper;
import io.machinecode.chainlink.jsl.fluent.partition.FluentPartition;
import io.machinecode.chainlink.jsl.fluent.partition.FluentPlan;
import io.machinecode.chainlink.jsl.fluent.partition.FluentReducer;
import io.machinecode.chainlink.jsl.fluent.task.FluentBatchlet;
import io.machinecode.chainlink.jsl.fluent.task.FluentCheckpointAlgorithm;
import io.machinecode.chainlink.jsl.fluent.task.FluentChunk;
import io.machinecode.chainlink.jsl.fluent.task.FluentExceptionClass;
import io.machinecode.chainlink.jsl.fluent.task.FluentExceptionClassFilter;
import io.machinecode.chainlink.jsl.fluent.task.FluentItemProcessor;
import io.machinecode.chainlink.jsl.fluent.task.FluentItemReader;
import io.machinecode.chainlink.jsl.fluent.task.FluentItemWriter;
import io.machinecode.chainlink.jsl.fluent.transition.FluentEnd;
import io.machinecode.chainlink.jsl.fluent.transition.FluentFail;
import io.machinecode.chainlink.jsl.fluent.transition.FluentNext;
import io.machinecode.chainlink.jsl.fluent.transition.FluentStop;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Jsl {

    public static FluentJob job() {
        return new FluentJob();
    }

    public static FluentJob job(final String name) {
        return new FluentJob().setJslName(name);
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

    public static FluentStep<FluentBatchlet,FluentMapper> executionAsStepWithBatchletAndMapper() {
        return stepWithBatchletAndMapper();
    }

    public static FluentStep<FluentBatchlet,FluentPlan> executionAsStepWithBatchletAndPlan() {
        return stepWithBatchletAndPlan();
    }

    public static FluentStep<FluentChunk,FluentMapper> executionAsStepWithChunkAndMapper() {
        return stepWithChunkAndMapper();
    }

    public static FluentStep<FluentChunk,FluentPlan> executionAsStepWithChunkAndPlan() {
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

    public static FluentStep<FluentBatchlet,FluentMapper> stepWithBatchletAndMapper() {
        return new FluentStep<FluentBatchlet,FluentMapper>();
    }

    public static FluentStep<FluentBatchlet,FluentPlan> stepWithBatchletAndPlan() {
        return new FluentStep<FluentBatchlet,FluentPlan>();
    }

    public static FluentStep<FluentChunk,FluentMapper> stepWithChunkAndMapper() {
        return new FluentStep<FluentChunk,FluentMapper>();
    }

    public static FluentStep<FluentChunk,FluentPlan> stepWithChunkAndPlan() {
        return new FluentStep<FluentChunk,FluentPlan>();
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

    public static FluentPartition<FluentPlan> partitionWithPlan() {
        return new FluentPartition<FluentPlan>();
    }

    public static FluentPartition<FluentMapper> partitionWithMapper() {
        return new FluentPartition<FluentMapper>();
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

    public static FluentExceptionClassFilter skippableExceptionClasses() {
        return filter();
    }

    public static FluentExceptionClassFilter retryableExceptionClasses() {
        return filter();
    }

    public static FluentExceptionClassFilter noRollbackExceptionClasses() {
        return filter();
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
