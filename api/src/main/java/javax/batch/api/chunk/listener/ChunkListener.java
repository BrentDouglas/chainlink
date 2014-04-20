package javax.batch.api.chunk.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ChunkListener {

    void beforeChunk() throws Exception;

    void onError(Exception exception) throws Exception;

    void afterChunk() throws Exception;
}
