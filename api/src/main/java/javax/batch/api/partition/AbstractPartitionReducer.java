package javax.batch.api.partition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
