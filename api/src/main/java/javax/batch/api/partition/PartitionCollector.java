package javax.batch.api.partition;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface PartitionCollector {

    Serializable collectPartitionData() throws Exception;
}
