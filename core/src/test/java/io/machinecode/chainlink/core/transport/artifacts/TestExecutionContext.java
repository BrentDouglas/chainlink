package io.machinecode.chainlink.core.transport.artifacts;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.Item;
import io.machinecode.chainlink.spi.context.MutableJobContext;
import io.machinecode.chainlink.spi.context.MutableStepContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestExecutionContext implements ExecutionContext {

    final long jobExecutionId;

    public TestExecutionContext(final long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public Long getRestartJobExecutionId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isRestarting() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getJobExecutionId() {
        return jobExecutionId;
    }

    @Override
    public Long getStepExecutionId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long getPartitionExecutionId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MutableJobContext getJobContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MutableStepContext getStepContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setStepContext(final MutableStepContext stepContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Item[] getItems() {
        return new Item[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setItems(final Item... items) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRestartElementId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setRestartElementId(final String restartElementId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long getLastStepExecutionId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setLastStepExecutionId(final long lastStepExecutionId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long[] getPriorStepExecutionIds() {
        return new long[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPriorStepExecutionId(final long[] priorStepExecutionId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
