package javax.batch.api.chunk.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ChunkListener {

    void beforeChunk() throws Exception;

    void onError(Exception exception) throws Exception;

    void afterChunk() throws Exception;
}
