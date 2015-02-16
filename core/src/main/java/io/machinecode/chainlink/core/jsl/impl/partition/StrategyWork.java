package io.machinecode.chainlink.core.jsl.impl.partition;

import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.jsl.partition.Strategy;

import javax.batch.api.partition.PartitionPlan;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface StrategyWork extends Strategy, Serializable {

    PartitionPlan getPartitionPlan(final Configuration configuration, final ExecutionContextImpl context) throws Exception;
}
