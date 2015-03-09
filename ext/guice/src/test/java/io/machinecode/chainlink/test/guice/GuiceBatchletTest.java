package io.machinecode.chainlink.test.guice;

import io.machinecode.chainlink.core.execution.artifact.batchlet.ErrorBatchlet;
import io.machinecode.chainlink.core.execution.artifact.batchlet.FailProcessBatchlet;
import io.machinecode.chainlink.core.execution.artifact.batchlet.FailStopBatchlet;
import io.machinecode.chainlink.core.execution.artifact.batchlet.OverrideBatchlet;
import io.machinecode.chainlink.inject.guice.BindingProvider;
import io.machinecode.chainlink.inject.guice.GuiceArtifactLoader;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.core.execution.batchlet.BatchletTest;
import io.machinecode.chainlink.core.execution.artifact.batchlet.FailBatchlet;
import io.machinecode.chainlink.core.execution.artifact.batchlet.InjectedBatchlet;
import io.machinecode.chainlink.core.execution.artifact.batchlet.RunBatchlet;
import io.machinecode.chainlink.core.execution.artifact.batchlet.StopBatchlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.batch.api.Batchlet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
//@Ignore("Guice injector doesn't work properties deferring to the field name.")
public class GuiceBatchletTest extends BatchletTest {

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getArtifactLoaders().add().setValue(new GuiceArtifactLoader(new BindingProvider() {
            @Override
            public List<Binding> getBindings() {
                return new ArrayList<Binding>() {{
                    add(Binding.of(Batchlet.class, "failBatchlet", FailBatchlet.class));
                    add(Binding.of(Batchlet.class, "errorBatchlet", ErrorBatchlet.class));
                    add(Binding.of(Batchlet.class, "runBatchlet", RunBatchlet.class));
                    add(Binding.of(Batchlet.class, "injectedBatchlet", InjectedBatchlet.class));
                    add(Binding.of(Batchlet.class, "stopBatchlet", StopBatchlet.class));
                    add(Binding.of(Batchlet.class, "overrideBatchlet", OverrideBatchlet.class));
                    add(Binding.of(Batchlet.class, "failStopBatchlet", FailStopBatchlet.class));
                    add(Binding.of(Batchlet.class, "failProcessBatchlet", FailProcessBatchlet.class));
                }};
            }
        }));
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
