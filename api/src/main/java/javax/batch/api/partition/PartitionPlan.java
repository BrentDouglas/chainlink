package javax.batch.api.partition;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface PartitionPlan {

    void setPartitions(int partitions);

    void setPartitionsOverride(boolean override);

    boolean getPartitionsOverride();

    void setThreads(int threads);

    void setPartitionProperties(Properties[] properties);

    int getPartitions();

    int getThreads();

    Properties[] getPartitionProperties();
}
