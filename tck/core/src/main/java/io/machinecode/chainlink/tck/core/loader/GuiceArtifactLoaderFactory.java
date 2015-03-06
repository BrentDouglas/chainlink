package io.machinecode.chainlink.tck.core.loader;

import io.machinecode.chainlink.inject.guice.BindingProvider;
import io.machinecode.chainlink.inject.guice.GuiceArtifactLoader;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;

import javax.batch.api.Batchlet;
import javax.batch.api.Decider;
import javax.batch.api.chunk.CheckpointAlgorithm;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.api.chunk.ItemReader;
import javax.batch.api.chunk.ItemWriter;
import javax.batch.api.chunk.listener.ChunkListener;
import javax.batch.api.chunk.listener.ItemProcessListener;
import javax.batch.api.chunk.listener.ItemReadListener;
import javax.batch.api.chunk.listener.ItemWriteListener;
import javax.batch.api.chunk.listener.RetryProcessListener;
import javax.batch.api.chunk.listener.RetryReadListener;
import javax.batch.api.chunk.listener.RetryWriteListener;
import javax.batch.api.chunk.listener.SkipProcessListener;
import javax.batch.api.chunk.listener.SkipReadListener;
import javax.batch.api.chunk.listener.SkipWriteListener;
import javax.batch.api.listener.JobListener;
import javax.batch.api.listener.StepListener;
import javax.batch.api.partition.PartitionAnalyzer;
import javax.batch.api.partition.PartitionCollector;
import javax.batch.api.partition.PartitionMapper;
import javax.batch.api.partition.PartitionReducer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GuiceArtifactLoaderFactory implements ArtifactLoaderFactory {
    @Override
    public ArtifactLoader produce(final Dependencies dependencies, final PropertyLookup properties) {
        return new GuiceArtifactLoader(new BindingProvider() {
            @Override
            public List<Binding> getBindings() {
                return new ArrayList<Binding>() {{
                    add(Binding.of(JobListener.class, "artifactInstanceTestJobListener", com.ibm.jbatch.tck.artifacts.specialized.ArtifactInstanceTestJobListener.class));
                    add(Binding.of(StepListener.class, "artifactInstanceTestStepListener", com.ibm.jbatch.tck.artifacts.specialized.ArtifactInstanceTestStepListener.class));
                    add(Binding.of(ChunkListener.class, "artifactInstanceTestChunkListener", com.ibm.jbatch.tck.artifacts.specialized.ArtifactInstanceTestChunkListener.class));
                    add(Binding.of(ItemReader.class, "artifactInstanceTestReader", com.ibm.jbatch.tck.artifacts.specialized.ArtifactInstanceTestReader.class));
                    add(Binding.of(ItemWriter.class, "artifactInstanceTestWriter", com.ibm.jbatch.tck.artifacts.specialized.ArtifactInstanceTestWriter.class));
                    add(Binding.of(Batchlet.class, "batchletRestartStateMachineImpl", com.ibm.jbatch.tck.artifacts.specialized.BatchletRestartStateMachineImpl.class));
                    add(Binding.of(Batchlet.class, "batchletUsingStepContextImpl", com.ibm.jbatch.tck.artifacts.specialized.BatchletUsingStepContextImpl.class));
                    add(Binding.of(ChunkListener.class, "chunkOnErrorCheckpointListener", com.ibm.jbatch.tck.artifacts.specialized.ChunkOnErrorCheckpointListener.class));
                    add(Binding.of(Batchlet.class, "contextsGetIdJobContextTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.ContextsGetIdJobContextTestBatchlet.class));
                    add(Binding.of(Batchlet.class, "contextsGetIdStepContextTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.ContextsGetIdStepContextTestBatchlet.class));
                    add(Binding.of(StepListener.class, "countInvocationsStepListener", com.ibm.jbatch.tck.artifacts.reusable.CountInvocationsStepListener.class));
                    add(Binding.of(StepListener.class, "countInvocationsObjectParameterizationStepListener", com.ibm.jbatch.tck.artifacts.reusable.CountInvocationsObjectParameterizationStepListener.class));
                    add(Binding.of(JobListener.class, "deciderTestsJobListener", com.ibm.jbatch.tck.artifacts.specialized.DeciderTestsJobListener.class));
                    add(Binding.of(Batchlet.class, "deciderTestsBatchlet", com.ibm.jbatch.tck.artifacts.specialized.DeciderTestsBatchlet.class));
                    add(Binding.of(Decider.class, "deciderTestsDecider", com.ibm.jbatch.tck.artifacts.specialized.DeciderTestsDecider.class));
                    add(Binding.of(ItemWriter.class, "defaultValueArrayWriter", com.ibm.jbatch.tck.artifacts.specialized.DefaultValueArrayWriter.class));
                    add(Binding.of(ItemReader.class, "doSomethingArrayItemReaderImpl", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingArrayItemReaderImpl.class));
                    add(Binding.of(ItemProcessor.class, "doSomethingArrayItemProcessorImpl", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingArrayItemProcessorImpl.class));
                    add(Binding.of(ItemProcessor.class, "doSomethingItemProcessorImpl", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingItemProcessorImpl.class));
                    add(Binding.of(ItemReader.class, "doSomethingItemReaderImpl", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingItemReaderImpl.class));
                    add(Binding.of(ItemWriter.class, "doSomethingItemWriterImpl", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingItemWriterImpl.class));
                    add(Binding.of(ItemWriter.class, "doSomethingSimpleArrayWriter", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingSimpleArrayWriter.class));
                    add(Binding.of(ItemReader.class, "doSomethingSimpleTimeArrayReader", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingSimpleTimeArrayReader.class));
                    add(Binding.of(ItemWriter.class, "doSomethingSimpleTimeArrayWriter", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingSimpleTimeArrayWriter.class));
                    add(Binding.of(Batchlet.class, "failRestartBatchlet", com.ibm.jbatch.tck.artifacts.specialized.FailRestartBatchlet.class));
                    add(Binding.of(Batchlet.class, "flowTransitionToDecisionTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.FlowTransitionToDecisionTestBatchlet.class));
                    add(Binding.of(Batchlet.class, "flowTransitionWithinFlowTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.FlowTransitionWithinFlowTestBatchlet.class));
                    add(Binding.of(Decider.class, "flowTransitionToDecisionTestDecider", com.ibm.jbatch.tck.artifacts.specialized.FlowTransitionToDecisionTestDecider.class));
                    add(Binding.of(ItemReader.class, "inventoryInitReader", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryInitReader.class));
                    add(Binding.of(ItemProcessor.class, "inventoryInitProcessor", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryInitProcessor.class));
                    add(Binding.of(ItemWriter.class, "inventoryInitWriter", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryInitWriter.class));
                    add(Binding.of(StepListener.class, "inventoryStepListener", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryStepListener.class));
                    add(Binding.of(ItemReader.class, "inventoryReader", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryReader.class));
                    add(Binding.of(ItemProcessor.class, "inventoryProcessor", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryProcessor.class));
                    add(Binding.of(ItemWriter.class, "inventoryWriter", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryWriter.class));
                    add(Binding.of(CheckpointAlgorithm.class, "inventoryCheckpointAlgorithmNoOverride", com.ibm.jbatch.tck.artifacts.specialized.InventoryCheckpointAlgorithmNoOverride.class));
                    add(Binding.of(CheckpointAlgorithm.class, "inventoryCheckpointAlgorithmOverride150", com.ibm.jbatch.tck.artifacts.specialized.InventoryCheckpointAlgorithmOverride150.class));
                    add(Binding.of(Batchlet.class, "jobAttributesTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.JobAttributesTestBatchlet.class));
                    add(Binding.of(Batchlet.class, "jobContextTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.JobContextTestBatchlet.class));
                    add(Binding.of(Batchlet.class, "jobLevelPropertiesCountBatchlet", com.ibm.jbatch.tck.artifacts.specialized.JobLevelPropertiesCountBatchlet.class));
                    add(Binding.of(Batchlet.class, "jobLevelPropertiesShouldNotBeAvailableThroughStepContextBatchlet", com.ibm.jbatch.tck.artifacts.specialized.JobLevelPropertiesShouldNotBeAvailableThroughStepContextBatchlet.class));
                    add(Binding.of(Batchlet.class, "jobLevelPropertiesPropertyValueBatchlet", com.ibm.jbatch.tck.artifacts.specialized.JobLevelPropertiesPropertyValueBatchlet.class));
                    add(Binding.of(ItemReader.class, "listenerOnErrorReader", com.ibm.jbatch.tck.artifacts.specialized.ListenerOnErrorReader.class));
                    add(Binding.of(ItemProcessor.class, "listenerOnErrorProcessor", com.ibm.jbatch.tck.artifacts.specialized.ListenerOnErrorProcessor.class));
                    add(Binding.of(ItemWriter.class, "listenerOnErrorWriter", com.ibm.jbatch.tck.artifacts.specialized.ListenerOnErrorWriter.class));
                    add(Binding.of(StepListener.class, "metricsStepListener", com.ibm.jbatch.tck.artifacts.specialized.MetricsStepListener.class));
                    add(Binding.of(Batchlet.class, "multipleExitStatusBatchlet", com.ibm.jbatch.tck.artifacts.specialized.MultipleExitStatusBatchlet.class));
                    add(Binding.of(Batchlet.class, "myBatchletImpl", com.ibm.jbatch.tck.artifacts.reusable.MyBatchletImpl.class));
                    add(Binding.of(Batchlet.class, "myBatchletWithPropertiesImpl", com.ibm.jbatch.tck.artifacts.specialized.MyBatchletWithPropertiesImpl.class));
                    //Double
                    add(Binding.of(ChunkListener.class, "myChunkListener", com.ibm.jbatch.tck.artifacts.specialized.MyChunkListener.class));
                    add(Binding.of(StepListener.class, "myChunkListener", com.ibm.jbatch.tck.artifacts.specialized.MyChunkListener.class));
                    add(Binding.of(CheckpointAlgorithm.class, "myCustomCheckpointAlgorithm", com.ibm.jbatch.tck.artifacts.specialized.MyCustomCheckpointAlgorithm.class));
                    add(Binding.of(ChunkListener.class, "myCustomCheckpointListener", com.ibm.jbatch.tck.artifacts.specialized.MyCustomCheckpointListener.class));
                    add(Binding.of(ItemReadListener.class, "myItemReadListenerImpl", com.ibm.jbatch.tck.artifacts.specialized.MyItemReadListenerImpl.class));
                    add(Binding.of(ItemProcessListener.class, "myItemProcessListenerImpl", com.ibm.jbatch.tck.artifacts.specialized.MyItemProcessListenerImpl.class));
                    add(Binding.of(ItemWriteListener.class, "myItemWriteListenerImpl", com.ibm.jbatch.tck.artifacts.specialized.MyItemWriteListenerImpl.class));
                    add(Binding.of(Batchlet.class, "myLongRunningBatchlet", com.ibm.jbatch.tck.artifacts.specialized.MyLongRunningBatchletImpl.class));
                    add(Binding.of(RetryReadListener.class, "myMultipleExceptionsRetryReadListener", com.ibm.jbatch.tck.artifacts.specialized.MyMultipleExceptionsRetryReadListener.class));
                    add(Binding.of(PartitionAnalyzer.class, "myPartitionAnalyzer", com.ibm.jbatch.tck.artifacts.specialized.MyPartitionAnalyzer.class));
                    add(Binding.of(PartitionCollector.class, "myPartitionCollector", com.ibm.jbatch.tck.artifacts.specialized.MyPartitionCollector.class));
                    add(Binding.of(PartitionReducer.class, "myPartitionReducer", com.ibm.jbatch.tck.artifacts.specialized.MyPartitionReducer.class));
                    add(Binding.of(Batchlet.class, "myParallelSubJobsExitStatusBatchlet", com.ibm.jbatch.tck.artifacts.reusable.MyParallelSubJobsExitStatusBatchlet.class));
                    add(Binding.of(Batchlet.class, "myPartitionedBatchletImpl", com.ibm.jbatch.tck.artifacts.specialized.MyPartitionedBatchletImpl.class));
                    add(Binding.of(PartitionMapper.class, "myPartitionMapper", com.ibm.jbatch.tck.artifacts.specialized.MyPartitionMapper.class));
                    add(Binding.of(RetryReadListener.class, "myRetryReadListener", com.ibm.jbatch.tck.artifacts.specialized.MyRetryReadListener.class));
                    add(Binding.of(RetryProcessListener.class, "myRetryProcessListener", com.ibm.jbatch.tck.artifacts.specialized.MyRetryProcessListener.class));
                    add(Binding.of(RetryWriteListener.class, "myRetryWriteListener", com.ibm.jbatch.tck.artifacts.specialized.MyRetryWriteListener.class));
                    add(Binding.of(CheckpointAlgorithm.class, "mySimpleCustomCheckpointAlgorithm", com.ibm.jbatch.tck.artifacts.specialized.MySimpleCustomCheckpointAlgorithm.class));
                    add(Binding.of(SkipProcessListener.class, "mySkipProcessListener", com.ibm.jbatch.tck.artifacts.specialized.MySkipProcessListener.class));
                    add(Binding.of(SkipReadListener.class, "mySkipReaderExceedListener", com.ibm.jbatch.tck.artifacts.specialized.MySkipReaderExceedListener.class));
                    add(Binding.of(SkipReadListener.class, "mySkipReadListener", com.ibm.jbatch.tck.artifacts.specialized.MySkipReadListener.class));
                    add(Binding.of(SkipWriteListener.class, "mySkipWriteListener", com.ibm.jbatch.tck.artifacts.specialized.MySkipWriteListener.class));
                    add(Binding.of(ChunkListener.class, "myTimeCheckpointListener", com.ibm.jbatch.tck.artifacts.specialized.MyTimeCheckpointListener.class));
                    //Double
                    add(Binding.of(JobListener.class, "myUniversalListener", com.ibm.jbatch.tck.artifacts.specialized.MyUniversalListener.class));
                    add(Binding.of(StepListener.class, "myUniversalListener", com.ibm.jbatch.tck.artifacts.specialized.MyUniversalListener.class));
                    add(Binding.of(RetryReadListener.class, "numbersRetryReadListener", com.ibm.jbatch.tck.artifacts.specialized.NumbersRetryReadListener.class));
                    add(Binding.of(SkipReadListener.class, "numbersSkipReadListener", com.ibm.jbatch.tck.artifacts.specialized.NumbersSkipReadListener.class));
                    add(Binding.of(RetryProcessListener.class, "numbersRetryProcessListener", com.ibm.jbatch.tck.artifacts.specialized.NumbersRetryProcessListener.class));
                    add(Binding.of(SkipProcessListener.class, "numbersSkipProcessListener", com.ibm.jbatch.tck.artifacts.specialized.NumbersSkipProcessListener.class));
                    add(Binding.of(RetryWriteListener.class, "numbersRetryWriteListener", com.ibm.jbatch.tck.artifacts.specialized.NumbersRetryWriteListener.class));
                    add(Binding.of(SkipWriteListener.class, "numbersSkipWriteListener", com.ibm.jbatch.tck.artifacts.specialized.NumbersSkipWriteListener.class));
                    add(Binding.of(ItemReader.class, "nullChkPtInfoReader", com.ibm.jbatch.tck.artifacts.specialized.NullChkPtInfoReader.class));
                    add(Binding.of(ItemWriter.class, "nullChkPtInfoWriter", com.ibm.jbatch.tck.artifacts.specialized.NullChkPtInfoWriter.class));
                    add(Binding.of(Batchlet.class, "overrideOnAttributeValuesUponRestartBatchlet", com.ibm.jbatch.tck.artifacts.specialized.OverrideOnAttributeValuesUponRestartBatchlet.class));
                    add(Binding.of(PartitionAnalyzer.class, "parsingPartitionAnalyzer", com.ibm.jbatch.tck.artifacts.specialized.ParsingPartitionAnalyzer.class));
                    add(Binding.of(ItemReader.class, "retryReader", com.ibm.jbatch.tck.artifacts.chunkartifacts.RetryReader.class));
                    add(Binding.of(ItemProcessor.class, "retryProcessor", com.ibm.jbatch.tck.artifacts.chunkartifacts.RetryProcessor.class));
                    add(Binding.of(ItemWriter.class, "retryWriter", com.ibm.jbatch.tck.artifacts.chunkartifacts.RetryWriter.class));
                    add(Binding.of(JobListener.class, "simpleJobListener", com.ibm.jbatch.tck.artifacts.reusable.SimpleJobListener.class));
                    add(Binding.of(ItemProcessor.class, "skipProcessor", com.ibm.jbatch.tck.artifacts.specialized.SkipProcessor.class));
                    add(Binding.of(ItemReader.class, "skipReader", com.ibm.jbatch.tck.artifacts.specialized.SkipReader.class));
                    add(Binding.of(ItemReader.class, "skipReaderMultipleExceptions", com.ibm.jbatch.tck.artifacts.specialized.SkipReaderMultipleExceptions.class));
                    add(Binding.of(ItemWriter.class, "skipWriter", com.ibm.jbatch.tck.artifacts.specialized.SkipWriter.class));
                    add(Binding.of(Batchlet.class, "splitFlowTransitionLoopTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.SplitFlowTransitionLoopTestBatchlet.class));
                    add(Binding.of(Batchlet.class, "splitTransitionToDecisionTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.SplitTransitionToDecisionTestBatchlet.class));
                    add(Binding.of(Decider.class, "splitTransitionToDecisionTestDecider", com.ibm.jbatch.tck.artifacts.specialized.SplitTransitionToDecisionTestDecider.class));
                    add(Binding.of(Batchlet.class, "splitTransitionToStepTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.SplitTransitionToStepTestBatchlet.class));
                    add(Binding.of(JobListener.class, "startLimitJobListener", com.ibm.jbatch.tck.artifacts.specialized.StartLimitJobListener.class));
                    add(Binding.of(Batchlet.class, "startLimitStateMachineVariation1Batchlet", com.ibm.jbatch.tck.artifacts.specialized.StartLimitStateMachineVariation1Batchlet.class));
                    add(Binding.of(Batchlet.class, "startLimitStateMachineVariation2Batchlet", com.ibm.jbatch.tck.artifacts.specialized.StartLimitStateMachineVariation2Batchlet.class));
                    add(Binding.of(Batchlet.class, "startLimitStateMachineVariation3Batchlet", com.ibm.jbatch.tck.artifacts.specialized.StartLimitStateMachineVariation3Batchlet.class));
                    add(Binding.of(Batchlet.class, "stepContextTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.StepContextTestBatchlet.class));
                    add(Binding.of(Batchlet.class, "stepLevelPropertiesCountBatchlet", com.ibm.jbatch.tck.artifacts.specialized.StepLevelPropertiesCountBatchlet.class));
                    add(Binding.of(Batchlet.class, "stepLevelPropertiesShouldNotBeAvailableThroughJobContextBatchlet", com.ibm.jbatch.tck.artifacts.specialized.StepLevelPropertiesShouldNotBeAvailableThroughJobContextBatchlet.class));
                    add(Binding.of(Batchlet.class, "stepLevelPropertiesPropertyValueBatchlet", com.ibm.jbatch.tck.artifacts.specialized.StepLevelPropertiesPropertyValueBatchlet.class));
                    add(Binding.of(JobListener.class, "threadTrackingJobListener", com.ibm.jbatch.tck.artifacts.specialized.ThreadTrackingJobListener.class));
                    add(Binding.of(StepListener.class, "threadTrackingStepListener", com.ibm.jbatch.tck.artifacts.specialized.ThreadTrackingStepListener.class));
                    add(Binding.of(Decider.class, "transitionDecider", com.ibm.jbatch.tck.artifacts.specialized.TransitionDecider.class));
                    add(Binding.of(Batchlet.class, "transitionTrackerBatchlet", com.ibm.jbatch.tck.artifacts.reusable.TransitionTrackerBatchlet.class));
                }};
            }
        });
    }
}
