package javax.batch.api.chunk;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class AbstractCheckpointAlgorithm implements CheckpointAlgorithm {

    @Override
    public int checkpointTimeout() throws Exception {
        return 0;
    }

    @Override
    public void beginCheckpoint() throws Exception {}

    @Override
    public void endCheckpoint() throws Exception {}
}
