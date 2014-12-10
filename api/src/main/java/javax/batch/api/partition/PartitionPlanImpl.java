package javax.batch.api.partition;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PartitionPlanImpl implements PartitionPlan {

    private int partitions = 0;
    private boolean override = false;
    private int threads = 0;
    private Properties[] properties = null;

    @Override
    public void setPartitions(int partitions) {
        this.partitions = partitions;
    }

    @Override
    public void setPartitionsOverride(boolean override) {
        this.override = override;
    }

    @Override
    public boolean getPartitionsOverride() {
        return this.override;
    }

    @Override
    public void setThreads(int threads) {
        this.threads = threads;
    }

    @Override
    public void setPartitionProperties(Properties[] properties) {
        this.properties = properties;
    }

    @Override
    public int getPartitions() {
        return this.partitions;
    }

    @Override
    public int getThreads() {
        return this.threads == 0 ? this.partitions : this.threads;
    }

    @Override
    public Properties[] getPartitionProperties() {
        return this.properties;
    }
}
