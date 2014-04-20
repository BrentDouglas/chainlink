package javax.batch.api.chunk;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class AbstractItemWriter implements ItemWriter {

    @Override
    public void open(Serializable checkpoint) throws Exception {}

    @Override
    public void close() throws Exception {}

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }
}
