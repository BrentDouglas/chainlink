package javax.batch.api.chunk.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class AbstractChunkListener implements ChunkListener {

    @Override
    public void beforeChunk() throws Exception {}

    @Override
    public void onError(Exception exception) throws Exception {}

    @Override
    public void afterChunk() throws Exception {}
}
