package javax.batch.api.chunk;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ItemReader {

    void open(Serializable checkpoint) throws Exception;

    void close() throws Exception;

    Object readItem() throws Exception;

    Serializable checkpointInfo() throws Exception;
}
