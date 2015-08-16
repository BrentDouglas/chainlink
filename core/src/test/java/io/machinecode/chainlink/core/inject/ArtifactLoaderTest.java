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
package io.machinecode.chainlink.core.inject;

import io.machinecode.chainlink.core.base.BaseTest;
import io.machinecode.chainlink.core.util.Tccl;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.core.execution.artifact.batchlet.RunBatchlet;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.api.Batchlet;
import javax.batch.api.Decider;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ArtifactLoaderTest extends BaseTest {

    @Test
    public void testInject() throws Exception {
        final ArtifactLoader loader = configuration().getArtifactLoader();
        final ClassLoader cl = Tccl.get();

        final Batchlet fromId = loader.load("runBatchlet", Batchlet.class, cl);
        Assert.assertNotNull(fromId);

        final Batchlet fromClass = loader.load(RunBatchlet.class.getCanonicalName(), Batchlet.class, cl);
        Assert.assertNotNull(fromClass);

        final Batchlet notAThing = loader.load("not a real thing", Batchlet.class, cl);
        Assert.assertNull(notAThing);

        try {
            loader.load("runBatchlet", Decider.class, cl);
            Assert.fail();
        } catch (final ArtifactOfWrongTypeException e) {
            //
        }
        try {
            loader.load(RunBatchlet.class.getCanonicalName(), Decider.class, cl);
            Assert.fail();
        } catch (final ArtifactOfWrongTypeException e) {
            //
        }
    }
}
