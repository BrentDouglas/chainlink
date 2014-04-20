package javax.batch.api.partition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class AbstractPartitionReducer implements PartitionReducer {

    @Override
    public void beginPartitionedStep() throws Exception {}

    @Override
    public void beforePartitionedStepCompletion() throws Exception {}

    @Override
    public void rollbackPartitionedStep() throws Exception {}

    @Override
    public void afterPartitionedStepCompletion(PartitionStatus status) throws Exception {}
}
