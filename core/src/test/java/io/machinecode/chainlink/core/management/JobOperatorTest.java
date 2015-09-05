/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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
package io.machinecode.chainlink.core.management;

import io.machinecode.chainlink.core.base.OperatorTest;
import io.machinecode.chainlink.core.context.MutableMetric;
import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.core.jsl.impl.JobImpl;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.repository.JobInstanceImpl;
import io.machinecode.chainlink.core.repository.MutableMetricImpl;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.spi.jsl.execution.Step;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.then.core.DeferredImpl;
import org.junit.Test;

import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobOperatorTest extends OperatorTest {

    private JobImpl _job() {
        return JobFactory.produce(Jsl.job("job")
                .addExecution(
                        Jsl.step("step1")
                                .setTask(
                                        Jsl.batchlet("runBatchlet")
                                )
                ).addExecution(
                        Jsl.step("step2")
                                .setTask(
                                        Jsl.batchlet("runBatchlet")
                                )
                ), PARAMETERS);
    }

    @Test
    public void getJobNames() throws Exception {
        printMethodName();
        {
            final Set<String> names = operator.getJobNames();
            assertEquals(0, names.size());
        }
        repository().createJobInstance("job1", "first", new Date());
        repository().createJobInstance("job1", "second", new Date());
        repository().createJobInstance("job2", "third", new Date());
        repository().createJobInstance("job3", "fourth", new Date());

        final Set<String> names = operator.getJobNames();
        assertEquals(3, names.size());
        assertTrue(names.contains("job1"));
        assertTrue(names.contains("job2"));
        assertTrue(names.contains("job3"));
    }

    @Test
    public void getJobInstanceCountTest() throws Exception {
        printMethodName();

        repository().createJobInstance("job1", "first", new Date());
        repository().createJobInstance("job1", "second", new Date());
        repository().createJobInstance("job2", "third", new Date());
        repository().createJobInstance("job3", "fourth", new Date());

        assertEquals(2, operator.getJobInstanceCount("job1"));
        assertEquals(1, operator.getJobInstanceCount("job2"));
        assertEquals(1, operator.getJobInstanceCount("job3"));
        try {
            operator.getJobInstanceCount("job4");
            fail();
        } catch (final NoSuchJobException e) {

        }
    }

    @Test
    public void getJobInstancesTest() throws Exception {
        printMethodName();

        repository().createJobInstance("job1", "first", new Date());
        repository().createJobInstance("job1", "second", new Date());
        repository().createJobInstance("job2", "third", new Date());
        repository().createJobInstance("job3", "fourth", new Date());

        assertEquals(2, operator.getJobInstances("job1", 0, 2).size());
        assertEquals(1, operator.getJobInstances("job1", 0, 1).size());
        assertEquals(1, operator.getJobInstances("job1", 1, 2).size());
        assertEquals(0, operator.getJobInstances("job1", 2, 3).size());
        assertEquals(1, operator.getJobInstances("job2", 0, 10).size());
        assertEquals(1, operator.getJobInstances("job3", 0, 10).size());
        try {
            operator.getJobInstances("job4", 0, 1);
            fail();
        } catch (final NoSuchJobException e) {

        }
    }

    //NoSuchJobException, JobSecurityException,
    @Test
    public void getRunningExecutionsTest() throws Exception {
        printMethodName();

        try {
            operator.getRunningExecutions("foo");
            fail();
        } catch (final NoSuchJobException e) {}

        final Properties parameters = new Properties();
        final ExtendedJobInstance first = repository().createJobInstance("job1", "first", new Date());
        final ExtendedJobInstance second = repository().createJobInstance("job1", "second", new Date());
        final ExtendedJobExecution fje = repository().createJobExecution(
                first.getInstanceId(),
                first.getJobName(),
                parameters,
                new Date()
        );
        {
            final List<Long> x = operator.getRunningExecutions("job1");
            assertTrue(x.contains(fje.getExecutionId()));
            assertEquals(1, x.size());
        }
        final ExtendedJobExecution sje = repository().createJobExecution(
                second.getInstanceId(),
                second.getJobName(),
                parameters,
                new Date()
        );
        {
            final List<Long> x = operator.getRunningExecutions("job1");
            assertTrue(x.contains(fje.getExecutionId()));
            assertTrue(x.contains(sje.getExecutionId()));
            assertEquals(2, x.size());
        }
        {
            repository().updateJobExecution(
                    fje.getExecutionId(),
                    BatchStatus.STARTED,
                    new Date()
            );
            final List<Long> x = operator.getRunningExecutions("job1");
            assertTrue(x.contains(fje.getExecutionId()));
            assertTrue(x.contains(sje.getExecutionId()));
            assertEquals(2, x.size());
        }
        {
            repository().updateJobExecution(
                    sje.getExecutionId(),
                    BatchStatus.STARTED,
                    new Date()
            );
            final List<Long> x = operator.getRunningExecutions("job1");
            assertTrue(x.contains(fje.getExecutionId()));
            assertTrue(x.contains(sje.getExecutionId()));
            assertEquals(2, x.size());
        }
        {
            repository().updateJobExecution(
                    fje.getExecutionId(),
                    BatchStatus.STOPPING,
                    new Date()
            );
            final List<Long> x = operator.getRunningExecutions("job1");
            assertTrue(x.contains(fje.getExecutionId()));
            assertTrue(x.contains(sje.getExecutionId()));
            assertEquals(2, x.size());
        }
        {
            repository().updateJobExecution(
                    fje.getExecutionId(),
                    BatchStatus.STOPPED,
                    new Date()
            );
            final List<Long> x = operator.getRunningExecutions("job1");
            assertTrue(x.contains(sje.getExecutionId()));
            assertEquals(1, x.size());
        }
        {
            repository().updateJobExecution(
                    fje.getExecutionId(),
                    BatchStatus.COMPLETED,
                    new Date()
            );
            final List<Long> x = operator.getRunningExecutions("job1");
            assertTrue(x.contains(sje.getExecutionId()));
            assertEquals(1, x.size());
        }
        {
            repository().updateJobExecution(
                    fje.getExecutionId(),
                    BatchStatus.FAILED,
                    new Date()
            );
            final List<Long> x = operator.getRunningExecutions("job1");
            assertTrue(x.contains(sje.getExecutionId()));
            assertEquals(1, x.size());
        }
        {
            repository().updateJobExecution(
                    fje.getExecutionId(),
                    BatchStatus.ABANDONED,
                    new Date()
            );
            final List<Long> x = operator.getRunningExecutions("job1");
            assertTrue(x.contains(sje.getExecutionId()));
            assertEquals(1, x.size());
        }
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getParametersTest() throws Exception {
        printMethodName();
        try {
            operator.getParameters(1);
            fail();
        } catch (final NoSuchJobExecutionException e) {}

        final ExtendedJobInstance first = repository().createJobInstance("job1", "first", new Date());
        final ExtendedJobInstance second = repository().createJobInstance("job1", "second", new Date());


        final Properties pfje = new Properties();
        pfje.put("test", "value");
        final ExtendedJobExecution fje = repository().createJobExecution(
                first.getInstanceId(),
                first.getJobName(),
                pfje,
                new Date()
        );
        {
            final Properties props = operator.getParameters(fje.getExecutionId());
            assertEquals(1, props.size());
            assertEquals("value", props.getProperty("test"));
        }
        final Properties def = new Properties();
        def.put("foo", "asdf");
        def.put("bar", "baz");
        final Properties psje = new Properties(def);
        psje.put("foo", "bar");
        psje.put("baz", "asd");
        final ExtendedJobExecution sje = repository().createJobExecution(
                second.getInstanceId(),
                second.getJobName(),
                psje,
                new Date()
        );
        {
            final Properties props = operator.getParameters(sje.getExecutionId());
            assertTrue(props.size() == 2 || props.size() == 3); //TODO Not sure about best way to handle this
            assertEquals("bar", props.getProperty("foo"));
            assertEquals("baz", props.getProperty("bar"));
            assertEquals("asd", props.getProperty("baz"));
        }
    }

    @Test
    public void getJobInstanceTest() throws Exception {
        printMethodName();
        final Properties parameters = new Properties();

        final ExtendedJobInstance fji = repository().createJobInstance("job1", "first", new Date());
        final ExtendedJobInstance sji = repository().createJobInstance("job1", "second", new Date());
        final ExtendedJobExecution fje = repository().createJobExecution(
                fji.getInstanceId(),
                fji.getJobName(),
                parameters,
                new Date()
        );
        final ExtendedJobExecution sje = repository().createJobExecution(
                sji.getInstanceId(),
                sji.getJobName(),
                parameters,
                new Date()
        );

        assertEquals(fji.getInstanceId(), operator.getJobInstance(fje.getExecutionId()).getInstanceId());
        assertEquals(sji.getInstanceId(), operator.getJobInstance(sje.getExecutionId()).getInstanceId());
        try {
            operator.getJobInstance(fje.getExecutionId() + sje.getExecutionId());
            fail();
        } catch (final NoSuchJobExecutionException e) {

        }
    }

    @Test
    public void getJobOperatorionTest() throws Exception {
        printMethodName();
        final Properties parameters = new Properties();

        final ExtendedJobInstance fji = repository().createJobInstance("job1", "first", new Date());
        final ExtendedJobInstance sji = repository().createJobInstance("job1", "second", new Date());
        final ExtendedJobExecution fje = repository().createJobExecution(
                fji.getInstanceId(),
                fji.getJobName(),
                parameters,
                new Date()
        );
        final ExtendedJobExecution sje = repository().createJobExecution(
                sji.getInstanceId(),
                sji.getJobName(),
                parameters,
                new Date()
        );

        final ChainImpl<Void> fc = new ChainImpl<>();
        final ChainImpl<Void> sc = new ChainImpl<>();

        registry().registerJob(fje.getExecutionId(), new UUIDId(configuration().getTransport()), fc);
        registry().registerJob(sje.getExecutionId(), new UUIDId(configuration().getTransport()), sc);

        assertNotNull(operator.getJobOperation(fje.getExecutionId()));
        assertNotNull(operator.getJobOperation(sje.getExecutionId()));
        try {
            operator.getJobOperation(fje.getExecutionId() + sje.getExecutionId());
            fail();
        } catch (final JobExecutionNotRunningException e) {

        }
    }

    private JobInstanceImpl _ji(final long id) {
        return new JobInstanceImpl.Builder().setJobInstanceId(id).build();
    }

    //NoSuchJobInstanceException, JobSecurityException,
    @Test
    public void getJobExecutionsTest() throws Exception {
        printMethodName();
        try {
            operator.getJobExecutions(_ji(1));
            fail();
        } catch (final NoSuchJobInstanceException e) {
            //
        }
        final Properties parameters = new Properties();
        final ExtendedJobInstance first = repository().createJobInstance("job1", "first", new Date());
        final ExtendedJobInstance second = repository().createJobInstance("job1", "second", new Date());
        {
            final List<? extends JobExecution> jes = operator.getJobExecutions(_ji(first.getInstanceId()));
            assertEquals(0, jes.size());
        }
        {
            final List<? extends JobExecution> jes = operator.getJobExecutions(_ji(second.getInstanceId()));
            assertEquals(0, jes.size());
        }

        repository().createJobExecution(
                first.getInstanceId(),
                first.getJobName(),
                parameters,
                new Date()
        );
        repository().createJobExecution(
                second.getInstanceId(),
                second.getJobName(),
                parameters,
                new Date()
        );
        repository().createJobExecution(
                second.getInstanceId(),
                second.getJobName(),
                parameters,
                new Date()
        );

        final List<? extends JobExecution> fe = operator.getJobExecutions(_ji(first.getInstanceId()));
        assertEquals(1, fe.size());

        final List<? extends JobExecution> se = operator.getJobExecutions(_ji(second.getInstanceId()));
        assertEquals(2, se.size());
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getJobExecutionTest() throws Exception {
        printMethodName();
        final Properties parameters = new Properties();
        final ExtendedJobInstance first = repository().createJobInstance("job1", "first", new Date());
        final ExtendedJobInstance second = repository().createJobInstance("job1", "second", new Date());

        try {
            operator.getJobExecution(1);
            fail();
        } catch (final NoSuchJobExecutionException e) {
            //
        }
        final ExtendedJobExecution fje = repository().createJobExecution(
                first.getInstanceId(),
                first.getJobName(),
                parameters,
                new Date()
        );
        final ExtendedJobExecution sje = repository().createJobExecution(
                second.getInstanceId(),
                second.getJobName(),
                parameters,
                new Date()
        );

        final ExtendedJobExecution fe = operator.getJobExecution(fje.getExecutionId());
        assertNotNull(fe);
        final ExtendedJobExecution se = operator.getJobExecution(sje.getExecutionId());
        assertNotNull(se);
    }

    //JobRestartException, NoSuchJobExecutionException, NoSuchJobInstanceException, JobExecutionNotMostRecentException, JobSecurityException,
    @Test
    public void restartJobExecutionTest() throws Exception {
        printMethodName();
        //TODO
    }

    //NoSuchJobExecutionException, JobSecurityException,
    @Test
    public void getStepExecutionsTest() throws Exception {
        printMethodName();
        try {
            operator.getStepExecutions(1);
            fail();
        } catch (final NoSuchJobExecutionException e) {}

        final Properties parameters = new Properties();
        final ExtendedJobInstance first = repository().createJobInstance("job1", "first", new Date());
        final ExtendedJobInstance second = repository().createJobInstance("job1", "second", new Date());
        final ExtendedJobExecution fje = repository().createJobExecution(
                first.getInstanceId(),
                first.getJobName(),
                parameters,
                new Date()
        );
        final ExtendedJobExecution sje = repository().createJobExecution(
                second.getInstanceId(),
                second.getJobName(),
                parameters,
                new Date()
        );
        final ExtendedStepExecution fs1 = repository().createStepExecution(
                fje.getExecutionId(),
                "foo",
                new Date()
        );
        try {
            operator.getStepExecutions(sje.getExecutionId() + 1);
            fail();
        } catch (final NoSuchJobExecutionException e) {}
        {
            final List<StepExecution> x = operator.getStepExecutions(fje.getExecutionId());
            assertNotNull(x);
            assertEquals(1, x.size());
            RepositoryTest.assertStepExecutionsEqual(fs1, x.get(0));
        }
        {
            final List<StepExecution> x = operator.getStepExecutions(sje.getExecutionId());
            assertNotNull(x);
            assertEquals(0, x.size());
        }
        final ExtendedStepExecution ss1 = repository().createStepExecution(
                sje.getExecutionId(),
                "bar",
                new Date()
        );
        final ExtendedStepExecution ss2 = repository().createStepExecution(
                sje.getExecutionId(),
                "baz",
                new Date()
        );
        {
            final List<StepExecution> x = operator.getStepExecutions(fje.getExecutionId());
            assertNotNull(x);
            assertEquals(1, x.size());
            RepositoryTest.assertStepExecutionsEqual(fs1, x.get(0));
        }
        {
            final List<StepExecution> x = operator.getStepExecutions(sje.getExecutionId());
            assertNotNull(x);
            assertEquals(2, x.size());
            RepositoryTest.assertStepExecutionsEqual(ss1, x.get(0));
            RepositoryTest.assertStepExecutionsEqual(ss2, x.get(1));
        }
    }
}
