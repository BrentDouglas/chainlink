package io.machinecode.chainlink.test.guice;

import com.google.inject.Binder;
import com.google.inject.name.Names;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.inject.core.VetoInjector;
import io.machinecode.chainlink.inject.guice.BindingProvider;
import io.machinecode.chainlink.inject.guice.GuiceArtifactLoader;
import io.machinecode.chainlink.jsl.core.util.Triplet;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.ExecutorTest;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.FailBatchlet;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.InjectedBatchlet;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.RunBatchlet;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.StopBatchlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;

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
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
//@Ignore("Guice injector doesn't work properties deferring to the field name.")
public class GuiceLocalExecutorTest extends ExecutorTest {

    @Override
    protected Builder _configuration() {
        return super._configuration()
                .setArtifactLoaders(new GuiceArtifactLoader(new BindingProvider() {
                    @Override
                    public List<Triplet<Class<?>, String, Class<?>>> getBindings() {
                        return new ArrayList<Triplet<Class<?>, String, Class<?>>>() {{
                            add(Triplet.<Class<?>, String, Class<?>>of(Batchlet.class, "failBatchlet", FailBatchlet.class));
                            add(Triplet.<Class<?>, String, Class<?>>of(Batchlet.class, "runBatchlet", RunBatchlet.class));
                            add(Triplet.<Class<?>, String, Class<?>>of(Batchlet.class, "injectedBatchlet", InjectedBatchlet.class));
                            add(Triplet.<Class<?>, String, Class<?>>of(Batchlet.class, "stopBatchlet", StopBatchlet.class));
                        }};
                    }
                }))
                .setInjectors(new VetoInjector());
    }
    @Override
    protected ExecutionRepository _repository() {
        return new MemoryExecutionRepository();
    }

    @BeforeClass
    public static void beforeClass() {
        //
    }

    @AfterClass
    public static void afterClass() {
        //
    }
}
