package io.machinecode.chainlink.core.execution.artifact.partition;

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;

import javax.batch.api.partition.PartitionMapper;
import javax.batch.api.partition.PartitionPlan;
import javax.batch.api.partition.PartitionPlanImpl;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestMapper implements PartitionMapper {

    @Override
    public PartitionPlan mapPartitions() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.MAP);
        return new PartitionPlanImpl() {

            @Override
            public boolean getPartitionsOverride() {
                return false;
            }

            @Override
            public int getPartitions() {
                return 2;
            }

            @Override
            public int getThreads() {
                return 2;
            }

            @Override
            public Properties[] getPartitionProperties() {
                return new Properties[]{
                        new Properties(){{
                            put("foo","bar");
                        }},
                        new Properties(){{
                            put("foo","baz");
                        }}
                };
            }
        };
    }
}
