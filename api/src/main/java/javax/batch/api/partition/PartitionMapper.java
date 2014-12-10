package javax.batch.api.partition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface PartitionMapper {

    PartitionPlan mapPartitions() throws Exception;
}
