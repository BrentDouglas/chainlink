package javax.batch.api.chunk;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ItemReader {

    void open(Serializable checkpoint) throws Exception;

    void close() throws Exception;

    Object readItem() throws Exception;

    Serializable checkpointInfo() throws Exception;
}
