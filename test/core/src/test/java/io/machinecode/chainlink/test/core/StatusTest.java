package io.machinecode.chainlink.test.core;

import io.machinecode.chainlink.core.util.Statuses;
import junit.framework.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class StatusTest {

    @Test
    public void testExitStatus() {
        Assert.assertTrue(Statuses.matches("STARTING", "STARTING"));
        Assert.assertTrue(Statuses.matches("STARTED", "STARTED"));
        Assert.assertTrue(Statuses.matches("COMPLETED", "COMPLETED"));
        Assert.assertTrue(Statuses.matches("FAILED", "FAILED"));
        Assert.assertTrue(Statuses.matches("ABANDONED", "ABANDONED"));
        Assert.assertTrue(Statuses.matches("STOPPED", "STOPPED"));
        Assert.assertTrue(Statuses.matches("STOPPING", "STOPPING"));

        Assert.assertTrue(Statuses.matches("?TOPPING", "STOPPING"));
        Assert.assertTrue(Statuses.matches("STOPPIN?", "STOPPING"));
        Assert.assertTrue(Statuses.matches("STOP?ING", "STOPPING"));

        Assert.assertTrue(Statuses.matches("STOPPIN*", "STOPPING"));
        Assert.assertTrue(Statuses.matches("STOP*", "STOPPING"));
        Assert.assertTrue(Statuses.matches("STOP*ING", "STOPPING"));
        Assert.assertTrue(Statuses.matches("STOP*PING", "STOPPING"));
        Assert.assertTrue(Statuses.matches("S*ING", "STOPPING"));
        Assert.assertTrue(Statuses.matches("*", "STOPPING"));
        Assert.assertTrue(Statuses.matches("**", "STOPPING"));
        Assert.assertTrue(Statuses.matches("*?", "STOPPING"));
        Assert.assertTrue(Statuses.matches("?*", "STOPPING"));
        Assert.assertTrue(Statuses.matches("ST*?PI*?G", "STOPPING"));
        Assert.assertTrue(Statuses.matches("STOP*PI*NG", "STOPPING"));
        Assert.assertTrue(Statuses.matches("STOP*PI?G", "STOPPING"));
    }
    @Test
    public void testBatchStatus() {
        Assert.assertTrue(Statuses.matches("STARTING", BatchStatus.STARTING));
        Assert.assertTrue(Statuses.matches("STARTED", BatchStatus.STARTED));
        Assert.assertTrue(Statuses.matches("COMPLETED", BatchStatus.COMPLETED));
        Assert.assertTrue(Statuses.matches("FAILED", BatchStatus.FAILED));
        Assert.assertTrue(Statuses.matches("ABANDONED", BatchStatus.ABANDONED));
        Assert.assertTrue(Statuses.matches("STOPPED", BatchStatus.STOPPED));
        Assert.assertTrue(Statuses.matches("STOPPING", BatchStatus.STOPPING));
    }
}
