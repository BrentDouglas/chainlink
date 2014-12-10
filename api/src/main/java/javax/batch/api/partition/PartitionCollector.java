package javax.batch.api.partition;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface PartitionCollector {

    Serializable collectPartitionData() throws Exception;
}
