package javax.batch.api.partition;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface PartitionAnalyzer {

    void analyzeCollectorData(Serializable data) throws Exception;

    void analyzeStatus(BatchStatus batchStatus, String exitStatus) throws Exception;
}
