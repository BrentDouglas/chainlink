package io.machinecode.chainlink.spi.work;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.partition.Strategy;

import javax.batch.api.partition.PartitionPlan;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface StrategyWork extends Strategy, Serializable {

    PartitionPlan getPartitionPlan(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception;
}
