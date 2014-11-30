package javax.batch.api.partition;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class AbstractPartitionAnalyzer implements PartitionAnalyzer {

    @Override
    public void analyzeCollectorData(Serializable data) throws Exception {}

    @Override
    public void analyzeStatus(BatchStatus batchStatus, String exitStatus) throws Exception {}
}
