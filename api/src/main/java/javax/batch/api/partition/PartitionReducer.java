package javax.batch.api.partition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface PartitionReducer {

    void beginPartitionedStep() throws Exception;

    void beforePartitionedStepCompletion() throws Exception;

    void rollbackPartitionedStep() throws Exception;

    void afterPartitionedStepCompletion(PartitionStatus status) throws Exception;

    enum PartitionStatus {
        COMMIT,
        ROLLBACK
    }
}
