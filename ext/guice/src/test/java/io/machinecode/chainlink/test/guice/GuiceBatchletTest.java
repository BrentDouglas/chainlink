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
