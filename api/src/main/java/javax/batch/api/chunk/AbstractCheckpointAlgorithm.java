package javax.batch.api.chunk;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
