package javax.batch.api.chunk;

import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ItemWriter {

    void open(Serializable checkpoint) throws Exception;

    void close() throws Exception;

    void writeItems(List<Object> items) throws Exception;

    Serializable checkpointInfo() throws Exception;
}
