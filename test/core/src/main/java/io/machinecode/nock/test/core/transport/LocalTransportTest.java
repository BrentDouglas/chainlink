package io.machinecode.nock.test.core.transport;

import io.machinecode.nock.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.nock.core.expression.JobPropertyContextImpl;
import io.machinecode.nock.core.factory.task.BatchletFactory;
import io.machinecode.nock.core.local.LocalRepository;
import io.machinecode.nock.core.local.LocalTransactionManager;
import io.machinecode.nock.core.local.LocalTransport;
import io.machinecode.nock.core.model.task.BatchletImpl;
import io.machinecode.nock.core.work.PlanImpl;
import io.machinecode.nock.core.work.task.RunTask;
import io.machinecode.nock.jsl.fluent.Jsl;
import io.machinecode.nock.jsl.util.Reference;
import io.machinecode.nock.spi.configuration.RuntimeConfiguration;
import io.machinecode.nock.spi.element.task.Batchlet;
import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.spi.loader.JobLoader;
import io.machinecode.nock.spi.transport.TargetThread;
import io.machinecode.nock.spi.work.Deferred;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class LocalTransportTest extends Assert {

    protected static RuntimeConfiguration configuration;
    private static Reference<Boolean> ranRunBatchlet = new Reference<Boolean>(false);
    private static Reference<Boolean> ranStopBatchlet = new Reference<Boolean>(false);
    private static Reference<Boolean> stoppedStopBatchlet = new Reference<Boolean>(false);

    private LocalTransport transport;

    public static Builder configuration() {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setTransactionManager(new LocalTransactionManager(180))
                .setRepository(new LocalRepository())
                .setJobLoaders(new JobLoader[0])
                .setArtifactLoaders(new ArtifactLoader[0]);
    }

    @AfterClass
    public static void afterClass() {
        //
    }

    public static class RunBatchlet extends javax.batch.api.AbstractBatchlet {
        @Override
        public String process() throws Exception {
            ranRunBatchlet.set(true);
            return BatchStatus.COMPLETED.toString();
        }
    }

    public static class StopBatchlet extends javax.batch.api.AbstractBatchlet {
        @Override
        public String process() throws Exception {
            ranStopBatchlet.set(true);
            try {
                synchronized (this) {
                    wait();
                }
            } catch (final InterruptedException e) {
                //
            }
            return BatchStatus.COMPLETED.toString();
        }

        @Override
        public void stop() throws Exception {
            stoppedStopBatchlet.set(true);
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @Before
    public void before() {
        transport = new LocalTransport(configuration, 1);
    }

    @Test
    public void runBatchletTest() throws ExecutionException, InterruptedException {
        final BatchletImpl batchlet = BatchletFactory.INSTANCE.produceExecution(Jsl.batchlet().setRef("run-batchlet"), null, null, new JobPropertyContextImpl(new Properties()));
        final Deferred deferred = transport.execute(new PlanImpl(new RunTask(
                batchlet,
                null,
                180
        ), TargetThread.ANY, Batchlet.ELEMENT));
        deferred.get();
        Assert.assertTrue(ranRunBatchlet.get());
    }

    @Test
    public void stopBatchletTest() throws ExecutionException, InterruptedException {
        final BatchletImpl batchlet = BatchletFactory.INSTANCE.produceExecution(Jsl.batchlet().setRef("stop-batchlet"), null, null, new JobPropertyContextImpl(new Properties()));
        final Deferred deferred = transport.execute(new PlanImpl(new RunTask(
                batchlet,
                null,
                180
        ), TargetThread.ANY, Batchlet.ELEMENT));
        Thread.sleep(100);
        Assert.assertTrue(ranStopBatchlet.get());
        Assert.assertTrue(deferred.cancel(true));
        try {
            deferred.get();
            Assert.fail();
        }catch (final CancellationException e) {
            //
        }
        Assert.assertTrue(stoppedStopBatchlet.get());
    }
}
