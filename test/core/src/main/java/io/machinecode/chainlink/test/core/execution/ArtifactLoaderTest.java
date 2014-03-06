package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.spi.loader.ArtifactLoader;
import io.machinecode.chainlink.spi.loader.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.RunBatchlet;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.api.Batchlet;
import javax.batch.api.Decider;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class ArtifactLoaderTest extends BaseTest {

    @Test
    public void testInject() {
        final ArtifactLoader loader = configuration().getArtifactLoader();
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();

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
