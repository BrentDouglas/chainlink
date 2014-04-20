package javax.batch.api.partition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PartitionMapper {

    PartitionPlan mapPartitions() throws Exception;
}
