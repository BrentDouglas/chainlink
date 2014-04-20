package javax.batch.api.partition;

import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
