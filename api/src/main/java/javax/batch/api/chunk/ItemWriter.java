package javax.batch.api.chunk;

import java.io.Serializable;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ItemWriter {

    void open(Serializable checkpoint) throws Exception;

    void close() throws Exception;

    void writeItems(List<Object> items) throws Exception;

    Serializable checkpointInfo() throws Exception;
}
