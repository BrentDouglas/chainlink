package io.machinecode.chainlink.tck.guice;

import com.google.inject.Binder;
import com.google.inject.name.Names;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.inject.core.VetoInjector;
import io.machinecode.chainlink.inject.guice.BindingProvider;
import io.machinecode.chainlink.inject.guice.GuiceArtifactLoader;
import io.machinecode.chainlink.jsl.core.util.Triplet;
import io.machinecode.chainlink.repository.jpa.EntityManagerLookup;
import io.machinecode.chainlink.repository.jpa.JpaExecutionRepository;
import io.machinecode.chainlink.repository.jpa.ResourceLocalTransactionManagerLookup;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;

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
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JpaGuiceConfigurationFactory implements ConfigurationFactory {

    @Override
    public Configuration produce() throws Exception {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setRepository(new JpaExecutionRepository(new EntityManagerLookup() {
                    @Override
                    public EntityManagerFactory getEntityManagerFactory() {
                        return Persistence.createEntityManagerFactory("TestPU");
                    }
                }, new ResourceLocalTransactionManagerLookup()))
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .setArtifactLoaders(new GuiceArtifactLoader(new BindingProvider() {
                    @Override
                    public List<Triplet<Class<?>, String, Class<?>>> getBindings() {
                        return new ArrayList<Triplet<Class<?>, String, Class<?>>>(){{
                            add(Triplet.<Class<?>, String,Class<?>>of(JobListener.class, "artifactInstanceTestJobListener", com.ibm.jbatch.tck.artifacts.specialized.ArtifactInstanceTestJobListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(StepListener.class, "artifactInstanceTestStepListener", com.ibm.jbatch.tck.artifacts.specialized.ArtifactInstanceTestStepListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ChunkListener.class, "artifactInstanceTestChunkListener", com.ibm.jbatch.tck.artifacts.specialized.ArtifactInstanceTestChunkListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemReader.class, "artifactInstanceTestReader", com.ibm.jbatch.tck.artifacts.specialized.ArtifactInstanceTestReader.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemWriter.class, "artifactInstanceTestWriter", com.ibm.jbatch.tck.artifacts.specialized.ArtifactInstanceTestWriter.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "batchletRestartStateMachineImpl", com.ibm.jbatch.tck.artifacts.specialized.BatchletRestartStateMachineImpl.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "batchletUsingStepContextImpl", com.ibm.jbatch.tck.artifacts.specialized.BatchletUsingStepContextImpl.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ChunkListener.class, "chunkOnErrorCheckpointListener", com.ibm.jbatch.tck.artifacts.specialized.ChunkOnErrorCheckpointListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "contextsGetIdJobContextTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.ContextsGetIdJobContextTestBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "contextsGetIdStepContextTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.ContextsGetIdStepContextTestBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(StepListener.class, "countInvocationsStepListener", com.ibm.jbatch.tck.artifacts.reusable.CountInvocationsStepListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(StepListener.class, "countInvocationsObjectParameterizationStepListener", com.ibm.jbatch.tck.artifacts.reusable.CountInvocationsObjectParameterizationStepListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(JobListener.class, "deciderTestsJobListener", com.ibm.jbatch.tck.artifacts.specialized.DeciderTestsJobListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "deciderTestsBatchlet", com.ibm.jbatch.tck.artifacts.specialized.DeciderTestsBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Decider.class, "deciderTestsDecider", com.ibm.jbatch.tck.artifacts.specialized.DeciderTestsDecider.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemWriter.class, "defaultValueArrayWriter", com.ibm.jbatch.tck.artifacts.specialized.DefaultValueArrayWriter.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemReader.class, "doSomethingArrayItemReaderImpl", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingArrayItemReaderImpl.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemProcessor.class, "doSomethingArrayItemProcessorImpl", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingArrayItemProcessorImpl.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemProcessor.class, "doSomethingItemProcessorImpl", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingItemProcessorImpl.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemReader.class, "doSomethingItemReaderImpl", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingItemReaderImpl.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemWriter.class, "doSomethingItemWriterImpl", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingItemWriterImpl.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemWriter.class, "doSomethingSimpleArrayWriter", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingSimpleArrayWriter.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemReader.class, "doSomethingSimpleTimeArrayReader", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingSimpleTimeArrayReader.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemWriter.class, "doSomethingSimpleTimeArrayWriter", com.ibm.jbatch.tck.artifacts.specialized.DoSomethingSimpleTimeArrayWriter.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "failRestartBatchlet", com.ibm.jbatch.tck.artifacts.specialized.FailRestartBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "flowTransitionToDecisionTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.FlowTransitionToDecisionTestBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "flowTransitionWithinFlowTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.FlowTransitionWithinFlowTestBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Decider.class, "flowTransitionToDecisionTestDecider", com.ibm.jbatch.tck.artifacts.specialized.FlowTransitionToDecisionTestDecider.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemReader.class, "inventoryInitReader", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryInitReader.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemProcessor.class, "inventoryInitProcessor", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryInitProcessor.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemWriter.class, "inventoryInitWriter", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryInitWriter.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(StepListener.class, "inventoryStepListener", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryStepListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemReader.class, "inventoryReader", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryReader.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemProcessor.class, "inventoryProcessor", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryProcessor.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemWriter.class, "inventoryWriter", com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryWriter.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(CheckpointAlgorithm.class, "inventoryCheckpointAlgorithmNoOverride", com.ibm.jbatch.tck.artifacts.specialized.InventoryCheckpointAlgorithmNoOverride.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(CheckpointAlgorithm.class, "inventoryCheckpointAlgorithmOverride150", com.ibm.jbatch.tck.artifacts.specialized.InventoryCheckpointAlgorithmOverride150.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "jobAttributesTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.JobAttributesTestBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "jobContextTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.JobContextTestBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "jobLevelPropertiesCountBatchlet", com.ibm.jbatch.tck.artifacts.specialized.JobLevelPropertiesCountBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "jobLevelPropertiesShouldNotBeAvailableThroughStepContextBatchlet", com.ibm.jbatch.tck.artifacts.specialized.JobLevelPropertiesShouldNotBeAvailableThroughStepContextBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "jobLevelPropertiesPropertyValueBatchlet", com.ibm.jbatch.tck.artifacts.specialized.JobLevelPropertiesPropertyValueBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemReader.class, "listenerOnErrorReader", com.ibm.jbatch.tck.artifacts.specialized.ListenerOnErrorReader.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemProcessor.class, "listenerOnErrorProcessor", com.ibm.jbatch.tck.artifacts.specialized.ListenerOnErrorProcessor.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemWriter.class, "listenerOnErrorWriter", com.ibm.jbatch.tck.artifacts.specialized.ListenerOnErrorWriter.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(StepListener.class, "metricsStepListener", com.ibm.jbatch.tck.artifacts.specialized.MetricsStepListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "multipleExitStatusBatchlet", com.ibm.jbatch.tck.artifacts.specialized.MultipleExitStatusBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "myBatchletImpl", com.ibm.jbatch.tck.artifacts.reusable.MyBatchletImpl.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "myBatchletWithPropertiesImpl", com.ibm.jbatch.tck.artifacts.specialized.MyBatchletWithPropertiesImpl.class));
                            //Double
                            add(Triplet.<Class<?>, String,Class<?>>of(ChunkListener.class, "myChunkListener", com.ibm.jbatch.tck.artifacts.specialized.MyChunkListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(StepListener.class, "myChunkListener", com.ibm.jbatch.tck.artifacts.specialized.MyChunkListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(CheckpointAlgorithm.class, "myCustomCheckpointAlgorithm", com.ibm.jbatch.tck.artifacts.specialized.MyCustomCheckpointAlgorithm.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ChunkListener.class, "myCustomCheckpointListener", com.ibm.jbatch.tck.artifacts.specialized.MyCustomCheckpointListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemReadListener.class, "myItemReadListenerImpl", com.ibm.jbatch.tck.artifacts.specialized.MyItemReadListenerImpl.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemProcessListener.class, "myItemProcessListenerImpl", com.ibm.jbatch.tck.artifacts.specialized.MyItemProcessListenerImpl.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemWriteListener.class, "myItemWriteListenerImpl", com.ibm.jbatch.tck.artifacts.specialized.MyItemWriteListenerImpl.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "myLongRunningBatchlet", com.ibm.jbatch.tck.artifacts.specialized.MyLongRunningBatchletImpl.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(RetryReadListener.class, "myMultipleExceptionsRetryReadListener", com.ibm.jbatch.tck.artifacts.specialized.MyMultipleExceptionsRetryReadListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(PartitionAnalyzer.class, "myPartitionAnalyzer", com.ibm.jbatch.tck.artifacts.specialized.MyPartitionAnalyzer.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(PartitionCollector.class, "myPartitionCollector", com.ibm.jbatch.tck.artifacts.specialized.MyPartitionCollector.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(PartitionReducer.class, "myPartitionReducer", com.ibm.jbatch.tck.artifacts.specialized.MyPartitionReducer.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "myParallelSubJobsExitStatusBatchlet", com.ibm.jbatch.tck.artifacts.reusable.MyParallelSubJobsExitStatusBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "myPartitionedBatchletImpl", com.ibm.jbatch.tck.artifacts.specialized.MyPartitionedBatchletImpl.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(PartitionMapper.class, "myPartitionMapper", com.ibm.jbatch.tck.artifacts.specialized.MyPartitionMapper.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(RetryReadListener.class, "myRetryReadListener", com.ibm.jbatch.tck.artifacts.specialized.MyRetryReadListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(RetryProcessListener.class, "myRetryProcessListener", com.ibm.jbatch.tck.artifacts.specialized.MyRetryProcessListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(RetryWriteListener.class, "myRetryWriteListener", com.ibm.jbatch.tck.artifacts.specialized.MyRetryWriteListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(CheckpointAlgorithm.class, "mySimpleCustomCheckpointAlgorithm", com.ibm.jbatch.tck.artifacts.specialized.MySimpleCustomCheckpointAlgorithm.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(SkipProcessListener.class, "mySkipProcessListener", com.ibm.jbatch.tck.artifacts.specialized.MySkipProcessListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(SkipReadListener.class, "mySkipReaderExceedListener", com.ibm.jbatch.tck.artifacts.specialized.MySkipReaderExceedListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(SkipReadListener.class, "mySkipReadListener", com.ibm.jbatch.tck.artifacts.specialized.MySkipReadListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(SkipWriteListener.class, "mySkipWriteListener", com.ibm.jbatch.tck.artifacts.specialized.MySkipWriteListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ChunkListener.class, "myTimeCheckpointListener", com.ibm.jbatch.tck.artifacts.specialized.MyTimeCheckpointListener.class));
                            //Double
                            add(Triplet.<Class<?>, String,Class<?>>of(JobListener.class, "myUniversalListener", com.ibm.jbatch.tck.artifacts.specialized.MyUniversalListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(StepListener.class, "myUniversalListener", com.ibm.jbatch.tck.artifacts.specialized.MyUniversalListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(RetryReadListener.class, "numbersRetryReadListener", com.ibm.jbatch.tck.artifacts.specialized.NumbersRetryReadListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(SkipReadListener.class, "numbersSkipReadListener", com.ibm.jbatch.tck.artifacts.specialized.NumbersSkipReadListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(RetryProcessListener.class, "numbersRetryProcessListener", com.ibm.jbatch.tck.artifacts.specialized.NumbersRetryProcessListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(SkipProcessListener.class, "numbersSkipProcessListener", com.ibm.jbatch.tck.artifacts.specialized.NumbersSkipProcessListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(RetryWriteListener.class, "numbersRetryWriteListener", com.ibm.jbatch.tck.artifacts.specialized.NumbersRetryWriteListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(SkipWriteListener.class, "numbersSkipWriteListener", com.ibm.jbatch.tck.artifacts.specialized.NumbersSkipWriteListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemReader.class, "nullChkPtInfoReader", com.ibm.jbatch.tck.artifacts.specialized.NullChkPtInfoReader.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemWriter.class, "nullChkPtInfoWriter", com.ibm.jbatch.tck.artifacts.specialized.NullChkPtInfoWriter.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "overrideOnAttributeValuesUponRestartBatchlet", com.ibm.jbatch.tck.artifacts.specialized.OverrideOnAttributeValuesUponRestartBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(PartitionAnalyzer.class, "parsingPartitionAnalyzer", com.ibm.jbatch.tck.artifacts.specialized.ParsingPartitionAnalyzer.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemReader.class, "retryReader", com.ibm.jbatch.tck.artifacts.chunkartifacts.RetryReader.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemProcessor.class, "retryProcessor", com.ibm.jbatch.tck.artifacts.chunkartifacts.RetryProcessor.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemWriter.class, "retryWriter", com.ibm.jbatch.tck.artifacts.chunkartifacts.RetryWriter.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(JobListener.class, "simpleJobListener", com.ibm.jbatch.tck.artifacts.reusable.SimpleJobListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemProcessor.class, "skipProcessor", com.ibm.jbatch.tck.artifacts.specialized.SkipProcessor.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemReader.class, "skipReader", com.ibm.jbatch.tck.artifacts.specialized.SkipReader.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemReader.class, "skipReaderMultipleExceptions", com.ibm.jbatch.tck.artifacts.specialized.SkipReaderMultipleExceptions.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(ItemWriter.class, "skipWriter", com.ibm.jbatch.tck.artifacts.specialized.SkipWriter.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "splitFlowTransitionLoopTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.SplitFlowTransitionLoopTestBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "splitTransitionToDecisionTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.SplitTransitionToDecisionTestBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Decider.class, "splitTransitionToDecisionTestDecider", com.ibm.jbatch.tck.artifacts.specialized.SplitTransitionToDecisionTestDecider.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "splitTransitionToStepTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.SplitTransitionToStepTestBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(JobListener.class, "startLimitJobListener", com.ibm.jbatch.tck.artifacts.specialized.StartLimitJobListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "startLimitStateMachineVariation1Batchlet", com.ibm.jbatch.tck.artifacts.specialized.StartLimitStateMachineVariation1Batchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "startLimitStateMachineVariation2Batchlet", com.ibm.jbatch.tck.artifacts.specialized.StartLimitStateMachineVariation2Batchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "startLimitStateMachineVariation3Batchlet", com.ibm.jbatch.tck.artifacts.specialized.StartLimitStateMachineVariation3Batchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "stepContextTestBatchlet", com.ibm.jbatch.tck.artifacts.specialized.StepContextTestBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "stepLevelPropertiesCountBatchlet", com.ibm.jbatch.tck.artifacts.specialized.StepLevelPropertiesCountBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "stepLevelPropertiesShouldNotBeAvailableThroughJobContextBatchlet", com.ibm.jbatch.tck.artifacts.specialized.StepLevelPropertiesShouldNotBeAvailableThroughJobContextBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "stepLevelPropertiesPropertyValueBatchlet", com.ibm.jbatch.tck.artifacts.specialized.StepLevelPropertiesPropertyValueBatchlet.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(JobListener.class, "threadTrackingJobListener", com.ibm.jbatch.tck.artifacts.specialized.ThreadTrackingJobListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(StepListener.class, "threadTrackingStepListener", com.ibm.jbatch.tck.artifacts.specialized.ThreadTrackingStepListener.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Decider.class, "transitionDecider", com.ibm.jbatch.tck.artifacts.specialized.TransitionDecider.class));
                            add(Triplet.<Class<?>, String,Class<?>>of(Batchlet.class, "transitionTrackerBatchlet", com.ibm.jbatch.tck.artifacts.reusable.TransitionTrackerBatchlet.class));
                        }};
                    }
                }))
                .setInjectors(new VetoInjector())
                .build();
    }
}
