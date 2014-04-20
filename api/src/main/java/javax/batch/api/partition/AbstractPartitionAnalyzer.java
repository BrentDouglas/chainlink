package javax.batch.api.partition;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class AbstractPartitionAnalyzer implements PartitionAnalyzer {

    @Override
    public void analyzeCollectorData(Serializable data) throws Exception {}

    @Override
    public void analyzeStatus(BatchStatus batchStatus, String exitStatus) throws Exception {}
}
