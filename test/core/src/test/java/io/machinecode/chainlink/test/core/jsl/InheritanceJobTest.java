package io.machinecode.chainlink.test.core.jsl;

import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.jsl.core.inherit.InheritableJob;
import io.machinecode.chainlink.jsl.core.inherit.execution.InheritableFlow;
import io.machinecode.chainlink.jsl.core.inherit.execution.InheritableStep;
import io.machinecode.chainlink.jsl.core.loader.AbstractJobLoader;
import io.machinecode.chainlink.spi.InheritableElement;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.element.Listener;
import io.machinecode.chainlink.spi.element.Property;
import io.machinecode.chainlink.spi.element.execution.Step;
import io.machinecode.chainlink.spi.element.partition.Plan;
import io.machinecode.chainlink.spi.element.task.Batchlet;
import io.machinecode.chainlink.spi.element.task.Chunk;
import io.machinecode.chainlink.spi.element.task.Chunk.CheckpointPolicy;
import io.machinecode.chainlink.spi.element.task.ItemProcessor;
import io.machinecode.chainlink.spi.element.task.ItemReader;
import io.machinecode.chainlink.spi.element.task.ItemWriter;
import io.machinecode.chainlink.spi.element.transition.Fail;
import io.machinecode.chainlink.spi.element.transition.Stop;
import io.machinecode.chainlink.test.core.ExpressionTest;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class InheritanceJobTest {

    private AbstractJobLoader repo = null;

    private static void testInheritance(final InheritableElement<?> inheritable) {
        Assert.assertNull(inheritable.isAbstract());
        Assert.assertNull(inheritable.getParent());
        Assert.assertNull(inheritable.getJslName());
    }

    @Before
    public void before() {
        if (repo == null) {
            repo = createRepo();
        }
    }

    protected abstract AbstractJobLoader createRepo();

    @Test
    public void jobConfigTest() throws Exception {
        final InheritableJob<?,?,?,?> job = repo.load("job-config-1");

        Assert.assertEquals("i1", job.getId());
    }

    @Test
    public void jobMergeTest1() throws JAXBException {
        final InheritableJob<?,?,?,?> job = repo.load("job-merge-1");
        final InheritableJob<?,?,?,?> result = repo.load("job-merge-1-result");

        testJob1(job);
        testJob1(result);
    }

    public static void testJob1(final InheritableJob<?,?,?,?> job) {
        Assert.assertEquals("job1", job.getId());
        Assert.assertEquals(2, job.getExecutions().size());
        Assert.assertNull(job.getListeners());
        Assert.assertNull(job.getProperties());
        Assert.assertEquals("1.0", job.getVersion());
        testInheritance(job);

        final InheritableStep<?,?,?,?,?,?> step1 = (InheritableStep<?,?,?,?,?,?>)job.getExecutions().get(0);
        Assert.assertEquals("s1", step1.getId());
        Assert.assertEquals("s2", step1.getNext());
        Assert.assertEquals("false", step1.getAllowStartIfComplete());
        Assert.assertEquals("0", step1.getStartLimit());
        Assert.assertNull(step1.getListeners());
        Assert.assertNull(step1.getProperties());
        testInheritance(step1);

        final Batchlet batchlet1 = (Batchlet)step1.getTask();
        Assert.assertNotNull(batchlet1);
        Assert.assertEquals("Doit", batchlet1.getRef());
        Assert.assertNull(batchlet1.getProperties());

        final InheritableStep<?,?,?,?,?,?> step2 = (InheritableStep<?,?,?,?,?,?>)job.getExecutions().get(1);
        Assert.assertEquals("s2", step2.getId());
        Assert.assertNull(step2.getNext());
        Assert.assertEquals("false", step2.getAllowStartIfComplete());
        Assert.assertEquals("0", step2.getStartLimit());
        Assert.assertNull(step2.getListeners());
        Assert.assertNull(step2.getProperties());
        testInheritance(step2);

        final Batchlet batchlet2 = (Batchlet)step2.getTask();
        Assert.assertNotNull(batchlet2);
        Assert.assertEquals("Doit2", batchlet2.getRef());
        Assert.assertNull(batchlet2.getProperties());
    }

    @Test
    public void jobMergeTest2() throws JAXBException {
        final InheritableJob<?,?,?,?> job = repo.load("job-merge-2");
        final InheritableJob<?,?,?,?> result = repo.load("job-merge-2-result");

        testJob2(job);
        testJob2(result);
    }

    public static void testJob2(final InheritableJob<?,?,?,?> job) {
        Assert.assertEquals("job1", job.getId());
        Assert.assertEquals(1, job.getExecutions().size());
        Assert.assertNull(job.getListeners());
        Assert.assertNull(job.getProperties());
        Assert.assertEquals("1.0", job.getVersion());
        testInheritance(job);

        final InheritableFlow<?,?,?> flow1 = (InheritableFlow<?,?,?>)job.getExecutions().get(0);
        Assert.assertEquals("f1", flow1.getId());
        Assert.assertEquals(2, flow1.getExecutions().size());
        Assert.assertEquals(1, flow1.getTransitions().size());
        Assert.assertNull(flow1.getNext());
        testInheritance(flow1);

        final InheritableStep<?,?,?,?,?,?> step1 = (InheritableStep<?,?,?,?,?,?>)flow1.getExecutions().get(0);
        Assert.assertEquals("s1", step1.getId());
        Assert.assertEquals("s2", step1.getNext());
        Assert.assertEquals("true", step1.getAllowStartIfComplete());
        Assert.assertEquals("0", step1.getStartLimit());
        Assert.assertNull(step1.getListeners());
        Assert.assertNull(step1.getProperties());
        testInheritance(step1);

        final Batchlet batchlet1 = (Batchlet)step1.getTask();
        Assert.assertNotNull(batchlet1);
        Assert.assertEquals("Doit", batchlet1.getRef());
        Assert.assertNull(batchlet1.getProperties());

        final InheritableStep<?,?,?,?,?,?> step2 = (InheritableStep<?,?,?,?,?,?>)flow1.getExecutions().get(1);
        Assert.assertEquals("s2", step2.getId());
        Assert.assertNull(step2.getNext());
        Assert.assertEquals("false", step2.getAllowStartIfComplete());
        Assert.assertEquals("0", step2.getStartLimit());
        Assert.assertNull(step2.getListeners());
        Assert.assertNull(step2.getProperties());
        testInheritance(step2);

        final Batchlet batchlet2 = (Batchlet)step2.getTask();
        Assert.assertNotNull(batchlet2);
        Assert.assertEquals("Doit2", batchlet2.getRef());
        Assert.assertNull(batchlet2.getProperties());

        final Stop stop1 = (Stop)flow1.getTransitions().get(0);
        Assert.assertEquals("ERROR", stop1.getOn());
        Assert.assertEquals("s1", stop1.getRestart());
        Assert.assertNull(stop1.getExitStatus());
    }

    @Test
    public void jobMergeTest3() throws JAXBException {
        final InheritableJob<?,?,?,?> job = repo.load("job-merge-3-2");
        final InheritableJob<?,?,?,?> result = repo.load("job-merge-3-result");

        testJob3(job);
        testJob3(result);
    }

    public static void testJob3(final InheritableJob<?,?,?,?> job) {
        Assert.assertEquals("job1", job.getId());
        Assert.assertEquals(2, job.getExecutions().size());
        Assert.assertNotNull(job.getListeners());
        Assert.assertEquals(1, job.getListeners().getListeners().size());
        Assert.assertNull(job.getProperties());
        Assert.assertEquals("1.0", job.getVersion());
        testInheritance(job);

        final InheritableStep<?,?,?,?,?,?> step1 = (InheritableStep<?,?,?,?,?,?>)job.getExecutions().get(0);
        Assert.assertEquals("s1", step1.getId());
        Assert.assertEquals("s2", step1.getNext());
        Assert.assertEquals("false", step1.getAllowStartIfComplete());
        Assert.assertEquals(Step.ZERO, step1.getStartLimit());
        Assert.assertNull(step1.getListeners());
        Assert.assertNull(step1.getProperties());
        testInheritance(step1);

        final Batchlet batchlet1 = (Batchlet)step1.getTask();
        Assert.assertNotNull(batchlet1);
        Assert.assertEquals("Doit", batchlet1.getRef());
        Assert.assertNull(batchlet1.getProperties());

        final InheritableStep<?,?,?,?,?,?> step2 = (InheritableStep<?,?,?,?,?,?>)job.getExecutions().get(1);
        Assert.assertEquals("s2", step2.getId());
        Assert.assertNull(step2.getNext());
        Assert.assertEquals("false", step2.getAllowStartIfComplete());
        Assert.assertEquals("0", step2.getStartLimit());
        Assert.assertNull(step2.getListeners());
        Assert.assertNull(step2.getProperties());
        testInheritance(step2);

        final Batchlet batchlet2 = (Batchlet)step2.getTask();
        Assert.assertNotNull(batchlet2);
        Assert.assertEquals("Doit2", batchlet2.getRef());
        Assert.assertNull(batchlet2.getProperties());

        final Listener listener = job.getListeners().getListeners().get(0);
        Assert.assertEquals("StepAuditor", listener.getRef());
    }

    @Test
    public void jobMergeTest4() throws JAXBException {
        final InheritableJob<?,?,?,?> job = repo.load("job-merge-4-2");
        final InheritableJob<?,?,?,?> result = repo.load("job-merge-4-result");

        //Same as 1
        testJob1(job);
        testJob1(result);
    }

    @Test
    public void jobMergeTest5() throws JAXBException {
        final InheritableJob<?,?,?,?> job = repo.load("job-merge-5-2");
        final InheritableJob<?,?,?,?> result = repo.load("job-merge-5-result");

        //Same as 3
        testJob3(job);
        testJob3(result);
    }

    @Test
    public void jobMergeTest6() throws JAXBException {
        final InheritableJob<?,?,?,?> job = repo.load("job-merge-6-2");
        final InheritableJob<?,?,?,?> result = repo.load("job-merge-6-result");

        testJob6(job);
        testJob6(result);
    }

    public static void testJob6(final InheritableJob<?,?,?,?> job) {
        Assert.assertEquals("job1", job.getId());
        Assert.assertEquals(2, job.getExecutions().size());
        Assert.assertNull(job.getListeners());
        Assert.assertNull(job.getProperties());
        Assert.assertEquals("1.0", job.getVersion());
        testInheritance(job);

        final InheritableFlow<?,?,?> flow1 = (InheritableFlow<?,?,?>)job.getExecutions().get(0);
        Assert.assertEquals("f1", flow1.getId());
        Assert.assertEquals(2, flow1.getExecutions().size());
        Assert.assertEquals(1, flow1.getTransitions().size());
        Assert.assertEquals("s3", flow1.getNext());
        testInheritance(flow1);

        final InheritableStep<?,?,?,?,?,?> step1 = (InheritableStep<?,?,?,?,?,?>)flow1.getExecutions().get(0);
        Assert.assertEquals("s1", step1.getId());
        Assert.assertEquals("s2", step1.getNext());
        Assert.assertEquals("true", step1.getAllowStartIfComplete());
        Assert.assertEquals(Step.ZERO, step1.getStartLimit());
        Assert.assertNull(step1.getListeners());
        Assert.assertNull(step1.getProperties());
        testInheritance(step1);

        final Batchlet batchlet1 = (Batchlet)step1.getTask();
        Assert.assertNotNull(batchlet1);
        Assert.assertEquals("Doit", batchlet1.getRef());
        Assert.assertNull(batchlet1.getProperties());

        final InheritableStep<?,?,?,?,?,?> step2 = (InheritableStep<?,?,?,?,?,?>)flow1.getExecutions().get(1);
        Assert.assertEquals("s2", step2.getId());
        Assert.assertNull(step2.getNext());
        Assert.assertEquals("false", step2.getAllowStartIfComplete());
        Assert.assertEquals(Step.ZERO, step2.getStartLimit());
        Assert.assertNull(step2.getListeners());
        Assert.assertNull(step2.getProperties());
        testInheritance(step2);

        final Batchlet batchlet2 = (Batchlet)step2.getTask();
        Assert.assertNotNull(batchlet2);
        Assert.assertEquals("Doit2", batchlet2.getRef());
        Assert.assertNull(batchlet2.getProperties());

        final Fail fail1 = (Fail)flow1.getTransitions().get(0);
        Assert.assertEquals("ERROR", fail1.getOn());
        Assert.assertNull(fail1.getExitStatus());

        final InheritableStep<?,?,?,?,?,?> step3 = (InheritableStep<?,?,?,?,?,?>)job.getExecutions().get(1);
        Assert.assertEquals("s3", step3.getId());
        Assert.assertNull(step3.getNext());
        Assert.assertEquals("false", step3.getAllowStartIfComplete());
        Assert.assertEquals(Step.ZERO, step3.getStartLimit());
        Assert.assertNull(step3.getListeners());
        Assert.assertNull(step3.getProperties());
        testInheritance(step3);

        final Batchlet batchlet3 = (Batchlet)step3.getTask();
        Assert.assertNotNull(batchlet3);
        Assert.assertEquals("Doit3", batchlet3.getRef());
        Assert.assertNull(batchlet3.getProperties());
    }

    @Test
    public void jobMergeTest7() throws JAXBException {
        final InheritableJob<?,?,?,?> job = repo.load("job-merge-7");
        final InheritableJob<?,?,?,?> result = repo.load("job-merge-7-result");

        testJob7(job);
        testJob7(result);
    }

    public static void testProperty(final List<? extends Property> properties, final int index, final String name , final String value) {
        final Property property = properties.get(index);
        Assert.assertEquals(name, property.getName());
        Assert.assertEquals(value, property.getValue());
    }

    public static void testJob7(final InheritableJob<?,?,?,?> job) {
        Assert.assertEquals("job1", job.getId());
        Assert.assertEquals(1, job.getExecutions().size());
        Assert.assertNull(job.getListeners());
        Assert.assertNull(job.getProperties());
        Assert.assertEquals("1.0", job.getVersion());
        testInheritance(job);

        final InheritableStep<?,?,?,?,?,?> step1 = (InheritableStep<?,?,?,?,?,?>)job.getExecutions().get(0);
        Assert.assertEquals("s1", step1.getId());
        Assert.assertNull(step1.getNext());
        Assert.assertEquals("false", step1.getAllowStartIfComplete());
        Assert.assertEquals(Step.ZERO, step1.getStartLimit());
        Assert.assertNull(step1.getListeners());
        Assert.assertNotNull(step1.getProperties());
        Assert.assertEquals(4, step1.getProperties().getProperties().size());
        testInheritance(step1);

        testProperty(step1.getProperties().getProperties(), 0, "debug", "false");
        testProperty(step1.getProperties().getProperties(), 1, "debug", "true");
        testProperty(step1.getProperties().getProperties(), 2, "step1.infile", "postings.out.txt");
        testProperty(step1.getProperties().getProperties(), 3, "step1.outfile", "postings.out.txt");

        final Chunk chunk1 = (Chunk) step1.getTask();
        Assert.assertNotNull(chunk1);
        Assert.assertEquals(CheckpointPolicy.ITEM, chunk1.getCheckpointPolicy());
        Assert.assertEquals("100", chunk1.getItemCount());
        Assert.assertEquals(Chunk.ZERO, chunk1.getTimeLimit());
        Assert.assertEquals(Chunk.MINUS_ONE, chunk1.getSkipLimit());
        Assert.assertEquals(Chunk.MINUS_ONE, chunk1.getRetryLimit());
        Assert.assertNotNull(chunk1.getReader());
        Assert.assertNotNull(chunk1.getProcessor());
        Assert.assertNotNull(chunk1.getWriter());
        Assert.assertNull(chunk1.getCheckpointAlgorithm());
        Assert.assertNull(chunk1.getSkippableExceptionClasses());
        Assert.assertNull(chunk1.getRetryableExceptionClasses());
        Assert.assertNull(chunk1.getNoRollbackExceptionClasses());

        final ItemReader reader1 = chunk1.getReader();
        Assert.assertEquals("PostingReader", reader1.getRef());
        Assert.assertNotNull(reader1.getProperties());
        Assert.assertEquals(1, reader1.getProperties().getProperties().size());
        testProperty(reader1.getProperties().getProperties(), 0, "infile", "#{jobProperties['step1.infile']}?:in.txt");

        final ItemProcessor processor1 = chunk1.getProcessor();
        Assert.assertEquals("PostingProcessing", processor1.getRef());

        final ItemWriter writer1 = chunk1.getWriter();
        Assert.assertEquals("PostingWriter", writer1.getRef());
        Assert.assertNotNull(writer1.getProperties());
        Assert.assertEquals(1, writer1.getProperties().getProperties().size());
        testProperty(writer1.getProperties().getProperties(), 0, "outfile", "#{jobProperties['step1.outfile']}?:out.txt");
    }

    @Test
    public void defaultValueTest() throws JAXBException {
        final InheritableJob<?,?,?,?> job = repo.load("job-default-1");

        testDefaults(JobFactory.produce(job, ExpressionTest.PARAMETERS));
    }

    public static void testDefaults(final Job job) {
        JobFactory.validate(job);
        Assert.assertEquals("1.0", job.getVersion());
        Assert.assertEquals("true", job.getRestartable());

        final Step step = (Step)job.getExecutions().get(0);
        Assert.assertNull(step.getNext());
        Assert.assertEquals("false", step.getAllowStartIfComplete());
        Assert.assertEquals(Step.ZERO, step.getStartLimit());

        final Plan plan = (Plan) step.getPartition().getStrategy();
        Assert.assertEquals(Plan.ONE, plan.getPartitions());
        Assert.assertEquals(Plan.ONE, plan.getThreads());

        final Chunk chunk = (Chunk) step.getTask();
        Assert.assertEquals(CheckpointPolicy.ITEM, chunk.getCheckpointPolicy());
        Assert.assertEquals(Chunk.TEN, chunk.getItemCount());
        Assert.assertEquals(Chunk.MINUS_ONE, chunk.getSkipLimit());
        Assert.assertEquals(Chunk.MINUS_ONE, chunk.getRetryLimit());
        Assert.assertEquals(Chunk.ZERO, chunk.getTimeLimit());
    }
}
