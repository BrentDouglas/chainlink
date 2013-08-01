package io.machinecode.nock.jsl.test;

import io.machinecode.nock.jsl.api.Job;
import io.machinecode.nock.jsl.api.chunk.Chunk;
import io.machinecode.nock.jsl.api.chunk.Chunk.CheckpointPolicy;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.xml.XmlBatchlet;
import io.machinecode.nock.jsl.xml.XmlJob;
import io.machinecode.nock.jsl.xml.XmlListener;
import io.machinecode.nock.jsl.xml.XmlProperty;
import io.machinecode.nock.jsl.xml.chunk.XmlChunk;
import io.machinecode.nock.jsl.xml.chunk.XmlItemProcessor;
import io.machinecode.nock.jsl.xml.chunk.XmlItemReader;
import io.machinecode.nock.jsl.xml.chunk.XmlItemWriter;
import io.machinecode.nock.jsl.xml.transition.XmlFail;
import io.machinecode.nock.jsl.xml.transition.XmlStop;
import io.machinecode.nock.jsl.xml.execution.XmlFlow;
import io.machinecode.nock.jsl.xml.execution.XmlStep;
import io.machinecode.nock.jsl.xml.util.Inheritable;
import junit.framework.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class XmlTest {

    private static ClassLoader classLoader = XmlTest.class.getClassLoader();

    private static void testInheritance(final Inheritable inheritable) {
        Assert.assertNull(inheritable.isAbstract());
        Assert.assertNull(inheritable.getParent());
        Assert.assertNull(inheritable.getJslName());
    }

    @Test
    public void jobConfigTest() throws JAXBException {
        final TestRepository repo = new TestRepository("Job.parent", classLoader.getResourceAsStream("job-config-1.xml"));
        final XmlJob job = repo.getJob();

        Assert.assertEquals("i1", job.getId());
    }

    @Test
    public void jobMergeTest1() throws JAXBException {
        final TestRepository repo = new TestRepository("Job.parent", classLoader.getResourceAsStream("job-merge-1.xml"));
        XmlJob job = repo.getJob();
        XmlJob result = repo.add("Result.parent", classLoader.getResourceAsStream("job-merge-1-result.xml"));

        job = job.inherit(repo);
        result = result.inherit(repo);

        testJob1(job);
        testJob1(result);
    }

    private static void testJob1(final XmlJob job) {
        Assert.assertEquals("job1", job.getId());
        Assert.assertEquals(2, job.getExecutions().size());
        Assert.assertNull(job.getListeners());
        Assert.assertNull(job.getProperties());
        Assert.assertEquals("1.0", job.getVersion());
        testInheritance(job);

        final XmlStep step1 = (XmlStep)job.getExecutions().get(0);
        Assert.assertEquals("s1", step1.getId());
        Assert.assertEquals("s2", step1.getNext());
        Assert.assertFalse(step1.isAllowStartIfComplete());
        Assert.assertEquals(0, step1.getStartLimit());
        Assert.assertNull(step1.getListeners());
        Assert.assertNull(step1.getProperties());
        testInheritance(step1);

        final XmlBatchlet batchlet1 = step1.getBatchlet();
        Assert.assertNotNull(batchlet1);
        Assert.assertEquals("Doit", batchlet1.getRef());
        Assert.assertNull(batchlet1.getProperties());

        final XmlStep step2 = (XmlStep)job.getExecutions().get(1);
        Assert.assertEquals("s2", step2.getId());
        Assert.assertNull(step2.getNext());
        Assert.assertFalse(step2.isAllowStartIfComplete());
        Assert.assertEquals(0, step2.getStartLimit());
        Assert.assertNull(step2.getListeners());
        Assert.assertNull(step2.getProperties());
        testInheritance(step2);

        final XmlBatchlet batchlet2 = step2.getBatchlet();
        Assert.assertNotNull(batchlet2);
        Assert.assertEquals("Doit2", batchlet2.getRef());
        Assert.assertNull(batchlet2.getProperties());
    }

    @Test
    public void jobMergeTest2() throws JAXBException {
        final TestRepository repo = new TestRepository("Job.parent", classLoader.getResourceAsStream("job-merge-2.xml"));
        XmlJob job = repo.getJob();
        XmlJob result = repo.add("Result.parent", classLoader.getResourceAsStream("job-merge-2-result.xml"));

        job = job.inherit(repo);
        result = result.inherit(repo);

        testJob2(job);
        testJob2(result);
    }

    private static void testJob2(final XmlJob job) {
        Assert.assertEquals("job1", job.getId());
        Assert.assertEquals(1, job.getExecutions().size());
        Assert.assertNull(job.getListeners());
        Assert.assertNull(job.getProperties());
        Assert.assertEquals("1.0", job.getVersion());
        testInheritance(job);

        final XmlFlow flow1 = (XmlFlow)job.getExecutions().get(0);
        Assert.assertEquals("f1", flow1.getId());
        Assert.assertEquals(2, flow1.getExecutions().size());
        Assert.assertEquals(1, flow1.getTransitions().size());
        Assert.assertNull(flow1.getNext());
        testInheritance(flow1);

        final XmlStep step1 = (XmlStep)flow1.getExecutions().get(0);
        Assert.assertEquals("s1", step1.getId());
        Assert.assertEquals("s2", step1.getNext());
        Assert.assertTrue(step1.isAllowStartIfComplete());
        Assert.assertEquals(0, step1.getStartLimit());
        Assert.assertNull(step1.getListeners());
        Assert.assertNull(step1.getProperties());
        testInheritance(step1);

        final XmlBatchlet batchlet1 = step1.getBatchlet();
        Assert.assertNotNull(batchlet1);
        Assert.assertEquals("Doit", batchlet1.getRef());
        Assert.assertNull(batchlet1.getProperties());

        final XmlStep step2 = (XmlStep)flow1.getExecutions().get(1);
        Assert.assertEquals("s2", step2.getId());
        Assert.assertNull(step2.getNext());
        Assert.assertFalse(step2.isAllowStartIfComplete());
        Assert.assertEquals(0, step2.getStartLimit());
        Assert.assertNull(step2.getListeners());
        Assert.assertNull(step2.getProperties());
        testInheritance(step2);

        final XmlBatchlet batchlet2 = step2.getBatchlet();
        Assert.assertNotNull(batchlet2);
        Assert.assertEquals("Doit2", batchlet2.getRef());
        Assert.assertNull(batchlet2.getProperties());

        final XmlStop stop1 = (XmlStop)flow1.getTransitions().get(0);
        Assert.assertEquals("ERROR", stop1.getOn());
        Assert.assertEquals("s1", stop1.getRestart());
        Assert.assertNull(stop1.getExitStatus());
    }

    @Test
    public void jobMergeTest3() throws JAXBException {
        final TestRepository repo = new TestRepository("Job.parent", classLoader.getResourceAsStream("job-merge-3-1.xml"));
        XmlJob first = repo.getJob();
        XmlJob second = repo.add("Child.parent", classLoader.getResourceAsStream("job-merge-3-2.xml"));
        XmlJob result = repo.add("Result.parent", classLoader.getResourceAsStream("job-merge-3-result.xml"));

        second = second.inherit(repo);
        result = result.inherit(repo);

        testJob3(second);
        testJob3(result);
    }

    private static void testJob3(final XmlJob job) {
        Assert.assertEquals("job1", job.getId());
        Assert.assertEquals(2, job.getExecutions().size());
        Assert.assertNotNull(job.getListeners());
        Assert.assertEquals(1, job.getListeners().getListeners().size());
        Assert.assertNull(job.getProperties());
        Assert.assertEquals("1.0", job.getVersion());
        testInheritance(job);

        final XmlStep step1 = (XmlStep)job.getExecutions().get(0);
        Assert.assertEquals("s1", step1.getId());
        Assert.assertEquals("s2", step1.getNext());
        Assert.assertFalse(step1.isAllowStartIfComplete());
        Assert.assertEquals(0, step1.getStartLimit());
        Assert.assertNull(step1.getListeners());
        Assert.assertNull(step1.getProperties());
        testInheritance(step1);

        final XmlBatchlet batchlet1 = step1.getBatchlet();
        Assert.assertNotNull(batchlet1);
        Assert.assertEquals("Doit", batchlet1.getRef());
        Assert.assertNull(batchlet1.getProperties());

        final XmlStep step2 = (XmlStep)job.getExecutions().get(1);
        Assert.assertEquals("s2", step2.getId());
        Assert.assertNull(step2.getNext());
        Assert.assertFalse(step2.isAllowStartIfComplete());
        Assert.assertEquals(0, step2.getStartLimit());
        Assert.assertNull(step2.getListeners());
        Assert.assertNull(step2.getProperties());
        testInheritance(step2);

        final XmlBatchlet batchlet2 = step2.getBatchlet();
        Assert.assertNotNull(batchlet2);
        Assert.assertEquals("Doit2", batchlet2.getRef());
        Assert.assertNull(batchlet2.getProperties());

        final XmlListener listener = job.getListeners().getListeners().get(0);
        Assert.assertEquals("StepAuditor", listener.getRef());
    }

    @Test
    public void jobMergeTest4() throws JAXBException {
        final TestRepository repo = new TestRepository("Job.parent", classLoader.getResourceAsStream("job-merge-4-1.xml"));
        XmlJob first = repo.getJob();
        XmlJob second = repo.add("Child.parent", classLoader.getResourceAsStream("job-merge-4-2.xml"));
        XmlJob result = repo.add("Result.parent", classLoader.getResourceAsStream("job-merge-4-result.xml"));

        second = second.inherit(repo);
        result = result.inherit(repo);

        //Same as 1
        testJob1(second);
        testJob1(result);
    }

    @Test
    public void jobMergeTest5() throws JAXBException {
        final TestRepository repo = new TestRepository("Job.parent", classLoader.getResourceAsStream("job-merge-5-1.xml"));
        XmlJob first = repo.getJob();
        XmlJob second = repo.add("Child.parent", classLoader.getResourceAsStream("job-merge-5-2.xml"));
        XmlJob result = repo.add("Result.parent", classLoader.getResourceAsStream("job-merge-5-result.xml"));

        second = second.inherit(repo);
        result = result.inherit(repo);

        //Same as 3
        testJob3(second);
        testJob3(result);
    }

    @Test
    public void jobMergeTest6() throws JAXBException {
        final TestRepository repo = new TestRepository("Job.parent", classLoader.getResourceAsStream("job-merge-6-1.xml"));
        XmlJob first = repo.getJob();
        XmlJob second = repo.add("Child.parent", classLoader.getResourceAsStream("job-merge-6-2.xml"));
        XmlJob result = repo.add("Result.parent", classLoader.getResourceAsStream("job-merge-6-result.xml"));

        second = second.inherit(repo);
        result = result.inherit(repo);

        testJob6(second);
        testJob6(result);
    }

    private static void testJob6(final XmlJob job) {
        Assert.assertEquals("job1", job.getId());
        Assert.assertEquals(2, job.getExecutions().size());
        Assert.assertNull(job.getListeners());
        Assert.assertNull(job.getProperties());
        Assert.assertEquals("1.0", job.getVersion());
        testInheritance(job);

        final XmlFlow flow1 = (XmlFlow)job.getExecutions().get(0);
        Assert.assertEquals("f1", flow1.getId());
        Assert.assertEquals(2, flow1.getExecutions().size());
        Assert.assertEquals(1, flow1.getTransitions().size());
        Assert.assertEquals("s3", flow1.getNext());
        testInheritance(flow1);

        final XmlStep step1 = (XmlStep)flow1.getExecutions().get(0);
        Assert.assertEquals("s1", step1.getId());
        Assert.assertEquals("s2", step1.getNext());
        Assert.assertTrue(step1.isAllowStartIfComplete());
        Assert.assertEquals(0, step1.getStartLimit());
        Assert.assertNull(step1.getListeners());
        Assert.assertNull(step1.getProperties());
        testInheritance(step1);

        final XmlBatchlet batchlet1 = step1.getBatchlet();
        Assert.assertNotNull(batchlet1);
        Assert.assertEquals("Doit", batchlet1.getRef());
        Assert.assertNull(batchlet1.getProperties());

        final XmlStep step2 = (XmlStep)flow1.getExecutions().get(1);
        Assert.assertEquals("s2", step2.getId());
        Assert.assertNull(step2.getNext());
        Assert.assertFalse(step2.isAllowStartIfComplete());
        Assert.assertEquals(0, step2.getStartLimit());
        Assert.assertNull(step2.getListeners());
        Assert.assertNull(step2.getProperties());
        testInheritance(step2);

        final XmlBatchlet batchlet2 = step2.getBatchlet();
        Assert.assertNotNull(batchlet2);
        Assert.assertEquals("Doit2", batchlet2.getRef());
        Assert.assertNull(batchlet2.getProperties());

        final XmlFail fail1 = (XmlFail)flow1.getTransitions().get(0);
        Assert.assertEquals("ERROR", fail1.getOn());
        Assert.assertNull(fail1.getExitStatus());

        final XmlStep step3 = (XmlStep)job.getExecutions().get(1);
        Assert.assertEquals("s3", step3.getId());
        Assert.assertNull(step3.getNext());
        Assert.assertFalse(step3.isAllowStartIfComplete());
        Assert.assertEquals(0, step3.getStartLimit());
        Assert.assertNull(step3.getListeners());
        Assert.assertNull(step3.getProperties());
        testInheritance(step3);

        final XmlBatchlet batchlet3 = step3.getBatchlet();
        Assert.assertNotNull(batchlet3);
        Assert.assertEquals("Doit3", batchlet3.getRef());
        Assert.assertNull(batchlet3.getProperties());
    }

    @Test
    public void jobMergeTest7() throws JAXBException {
        final TestRepository repo = new TestRepository("Job.parent", classLoader.getResourceAsStream("job-merge-7.xml"));
        XmlJob job = repo.getJob();
        XmlJob result = repo.add("Result.parent", classLoader.getResourceAsStream("job-merge-7-result.xml"));

        job = job.inherit(repo);
        result = result.inherit(repo);

        testJob7(job);
        testJob7(result);
    }

    private static void testProperty(final List<XmlProperty> properties, final int index, final String name , final String value) {
        final XmlProperty property = properties.get(index);
        Assert.assertEquals(name, property.getName());
        Assert.assertEquals(value, property.getValue());
    }

    private static void testJob7(final XmlJob job) {
        Assert.assertEquals("job1", job.getId());
        Assert.assertEquals(1, job.getExecutions().size());
        Assert.assertNull(job.getListeners());
        Assert.assertNull(job.getProperties());
        Assert.assertEquals("1.0", job.getVersion());
        testInheritance(job);

        final XmlStep step1 = (XmlStep)job.getExecutions().get(0);
        Assert.assertEquals("s1", step1.getId());
        Assert.assertNull(step1.getNext());
        Assert.assertFalse(step1.isAllowStartIfComplete());
        Assert.assertEquals(0, step1.getStartLimit());
        Assert.assertNull(step1.getListeners());
        Assert.assertNotNull(step1.getProperties());
        Assert.assertEquals(4, step1.getProperties().getProperties().size());
        testInheritance(step1);

        testProperty(step1.getProperties().getProperties(), 0, "debug", "false");
        testProperty(step1.getProperties().getProperties(), 1, "debug", "true");
        testProperty(step1.getProperties().getProperties(), 2, "step1.infile", "postings.out.txt");
        testProperty(step1.getProperties().getProperties(), 3, "step1.outfile", "postings.out.txt");

        final XmlChunk chunk1 = step1.getChunk();
        Assert.assertNotNull(chunk1);
        Assert.assertEquals(CheckpointPolicy.ITEM, chunk1.getCheckpointPolicy());
        Assert.assertEquals(100, chunk1.getItemCount());
        Assert.assertEquals(0, chunk1.getTimeLimit());
        Assert.assertEquals(0, chunk1.getSkipLimit());
        Assert.assertEquals(0, chunk1.getRetryLimit());
        Assert.assertNotNull(chunk1.getReader());
        Assert.assertNotNull(chunk1.getProcessor());
        Assert.assertNotNull(chunk1.getWriter());
        Assert.assertNull(chunk1.getCheckpointAlgorithm());
        Assert.assertNull(chunk1.getSkippableExceptionClasses());
        Assert.assertNull(chunk1.getRetryableExceptionClasses());
        Assert.assertNull(chunk1.getNoRollbackExceptionClasses());

        final XmlItemReader reader1 = chunk1.getReader();
        Assert.assertEquals("PostingReader", reader1.getRef());
        Assert.assertNotNull(reader1.getProperties());
        Assert.assertEquals(1, reader1.getProperties().getProperties().size());
        testProperty(reader1.getProperties().getProperties(), 0, "infile", "#{jobProperties['step1.infile']}?:in.txt");

        final XmlItemProcessor processor1 = chunk1.getProcessor();
        Assert.assertEquals("PostingProcessing", processor1.getRef());

        final XmlItemWriter writer1 = chunk1.getWriter();
        Assert.assertEquals("PostingWriter", writer1.getRef());
        Assert.assertNotNull(writer1.getProperties());
        Assert.assertEquals(1, writer1.getProperties().getProperties().size());
        testProperty(writer1.getProperties().getProperties(), 0, "outfile", "#{jobProperties['step1.outfile']}?:out.txt");
    }

    @Test
    public void defaultValueTest() throws JAXBException {
        final TestRepository repo = new TestRepository("Job.parent", classLoader.getResourceAsStream("job-default-1.xml"));
        XmlJob job = repo.getJob();

        job = job.inherit(repo);

        testDefaults(job.build());
    }

    public static void testDefaults(final Job job) {
        Assert.assertEquals("1.0", job.getVersion());
        Assert.assertTrue(job.isRestartable());

        final Step step = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step2", step.getNext());
        Assert.assertFalse(step.isAllowStartIfComplete());
        Assert.assertEquals(0, step.getStartLimit());

        final PartitionPlan plan = (PartitionPlan) step.getPartition().getMapper();
        Assert.assertEquals(1, plan.getPartitions());
        Assert.assertEquals(1, (int)plan.getThreads());

        final Chunk chunk = (Chunk) step.getPart();
        Assert.assertEquals(CheckpointPolicy.ITEM, chunk.getCheckpointPolicy());
        Assert.assertEquals(10, chunk.getItemCount());
        Assert.assertEquals(0, chunk.getSkipLimit());
        Assert.assertEquals(0, chunk.getRetryLimit());
        Assert.assertEquals(0, chunk.getTimeLimit());
    }
}
