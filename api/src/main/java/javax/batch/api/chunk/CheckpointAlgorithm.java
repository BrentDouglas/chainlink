package javax.batch.api.chunk;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface CheckpointAlgorithm {

    int checkpointTimeout() throws Exception;

    void beginCheckpoint() throws Exception;

    boolean isReadyToCheckpoint() throws Exception;

    void endCheckpoint() throws Exception;
}
