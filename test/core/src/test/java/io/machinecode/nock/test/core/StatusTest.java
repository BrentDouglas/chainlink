package io.machinecode.nock.test.core;

import io.machinecode.nock.core.work.RepositoryStatus;
import junit.framework.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StatusTest {

    @Test
    public void testExitStatus() {
        Assert.assertTrue(RepositoryStatus.matches("STARTING", "STARTING"));
        Assert.assertTrue(RepositoryStatus.matches("STARTED", "STARTED"));
        Assert.assertTrue(RepositoryStatus.matches("COMPLETED", "COMPLETED"));
        Assert.assertTrue(RepositoryStatus.matches("FAILED", "FAILED"));
        Assert.assertTrue(RepositoryStatus.matches("ABANDONED", "ABANDONED"));
        Assert.assertTrue(RepositoryStatus.matches("STOPPED", "STOPPED"));
        Assert.assertTrue(RepositoryStatus.matches("STOPPING", "STOPPING"));

        Assert.assertTrue(RepositoryStatus.matches("?TOPPING", "STOPPING"));
        Assert.assertTrue(RepositoryStatus.matches("STOPPIN?", "STOPPING"));
        Assert.assertTrue(RepositoryStatus.matches("STOP?ING", "STOPPING"));

        Assert.assertTrue(RepositoryStatus.matches("STOPPIN*", "STOPPING"));
        Assert.assertTrue(RepositoryStatus.matches("STOP*", "STOPPING"));
        Assert.assertTrue(RepositoryStatus.matches("STOP*ING", "STOPPING"));
        Assert.assertTrue(RepositoryStatus.matches("STOP*PING", "STOPPING"));
        Assert.assertTrue(RepositoryStatus.matches("S*ING", "STOPPING"));
        Assert.assertTrue(RepositoryStatus.matches("*", "STOPPING"));
        Assert.assertTrue(RepositoryStatus.matches("**", "STOPPING"));
        Assert.assertTrue(RepositoryStatus.matches("*?", "STOPPING"));
        Assert.assertTrue(RepositoryStatus.matches("?*", "STOPPING"));
        Assert.assertTrue(RepositoryStatus.matches("ST*?PI*?G", "STOPPING"));
        Assert.assertTrue(RepositoryStatus.matches("STOP*PI*NG", "STOPPING"));
        Assert.assertTrue(RepositoryStatus.matches("STOP*PI?G", "STOPPING"));
    }
    @Test
    public void testBatchStatus() {
        Assert.assertTrue(RepositoryStatus.matches("STARTING", BatchStatus.STARTING));
        Assert.assertTrue(RepositoryStatus.matches("STARTED", BatchStatus.STARTED));
        Assert.assertTrue(RepositoryStatus.matches("COMPLETED", BatchStatus.COMPLETED));
        Assert.assertTrue(RepositoryStatus.matches("FAILED", BatchStatus.FAILED));
        Assert.assertTrue(RepositoryStatus.matches("ABANDONED", BatchStatus.ABANDONED));
        Assert.assertTrue(RepositoryStatus.matches("STOPPED", BatchStatus.STOPPED));
        Assert.assertTrue(RepositoryStatus.matches("STOPPING", BatchStatus.STOPPING));
    }
}
