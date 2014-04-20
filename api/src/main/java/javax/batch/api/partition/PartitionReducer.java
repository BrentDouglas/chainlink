package javax.batch.api.partition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
