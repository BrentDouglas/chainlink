package javax.batch.api.partition;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PartitionCollector {

    Serializable collectPartitionData() throws Exception;
}
