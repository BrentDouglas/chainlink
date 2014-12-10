package javax.batch.api.chunk;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ItemReader {

    void open(Serializable checkpoint) throws Exception;

    void close() throws Exception;

    Object readItem() throws Exception;

    Serializable checkpointInfo() throws Exception;
}
