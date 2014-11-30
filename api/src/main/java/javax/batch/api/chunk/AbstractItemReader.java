package javax.batch.api.chunk;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class AbstractItemReader implements ItemReader {

    @Override
    public void open(Serializable checkpoint) throws Exception {}

    @Override
    public void close() throws Exception {}

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }
}
