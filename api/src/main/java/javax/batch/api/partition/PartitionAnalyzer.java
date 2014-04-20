package javax.batch.api.partition;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PartitionAnalyzer {

    void analyzeCollectorData(Serializable data) throws Exception;

    void analyzeStatus(BatchStatus batchStatus, String exitStatus) throws Exception;
}
