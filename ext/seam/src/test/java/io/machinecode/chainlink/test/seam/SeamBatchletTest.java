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
package io.machinecode.chainlink.test.seam;

import io.machinecode.chainlink.inject.seam.SeamArtifactLoader;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.core.execution.batchlet.BatchletTest;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.mock.MockServletContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SeamBatchletTest extends BatchletTest {

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getArtifactLoaders().add().setValue(SeamArtifactLoader.inject("seamArtifactLoader", SeamArtifactLoader.class));
    }

    @BeforeClass
    public static void beforeClass() {
        final ServletContext context = new MockServletContext();
        ServletLifecycle.beginApplication(context);
        new Initialization(context).create().init();
    }

    @AfterClass
    public static void AfterClass() {
        Lifecycle.endApplication();
    }
}
