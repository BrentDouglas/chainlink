package io.machinecode.nock.test.core;

import io.machinecode.nock.core.work.Status;
import junit.framework.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StatusTest {

    @Test
    public void testExitStatus() {
        Assert.assertTrue(Status.matches("STARTING", "STARTING"));
        Assert.assertTrue(Status.matches("STARTED", "STARTED"));
        Assert.assertTrue(Status.matches("COMPLETED", "COMPLETED"));
        Assert.assertTrue(Status.matches("FAILED", "FAILED"));
        Assert.assertTrue(Status.matches("ABANDONED", "ABANDONED"));
        Assert.assertTrue(Status.matches("STOPPED", "STOPPED"));
        Assert.assertTrue(Status.matches("STOPPING", "STOPPING"));

        Assert.assertTrue(Status.matches("?TOPPING", "STOPPING"));
        Assert.assertTrue(Status.matches("STOPPIN?", "STOPPING"));
        Assert.assertTrue(Status.matches("STOP?ING", "STOPPING"));

        Assert.assertTrue(Status.matches("STOPPIN*", "STOPPING"));
        Assert.assertTrue(Status.matches("STOP*", "STOPPING"));
        Assert.assertTrue(Status.matches("STOP*ING", "STOPPING"));
        Assert.assertTrue(Status.matches("STOP*PING", "STOPPING"));
        Assert.assertTrue(Status.matches("S*ING", "STOPPING"));
        Assert.assertTrue(Status.matches("*", "STOPPING"));
        Assert.assertTrue(Status.matches("**", "STOPPING"));
        Assert.assertTrue(Status.matches("*?", "STOPPING"));
        Assert.assertTrue(Status.matches("?*", "STOPPING"));
        Assert.assertTrue(Status.matches("ST*?PI*?G", "STOPPING"));
        Assert.assertTrue(Status.matches("STOP*PI*NG", "STOPPING"));
        Assert.assertTrue(Status.matches("STOP*PI?G", "STOPPING"));
    }
    @Test
    public void testBatchStatus() {
        Assert.assertTrue(Status.matches("STARTING", BatchStatus.STARTING));
        Assert.assertTrue(Status.matches("STARTED", BatchStatus.STARTED));
        Assert.assertTrue(Status.matches("COMPLETED", BatchStatus.COMPLETED));
        Assert.assertTrue(Status.matches("FAILED", BatchStatus.FAILED));
        Assert.assertTrue(Status.matches("ABANDONED", BatchStatus.ABANDONED));
        Assert.assertTrue(Status.matches("STOPPED", BatchStatus.STOPPED));
        Assert.assertTrue(Status.matches("STOPPING", BatchStatus.STOPPING));
    }
}
