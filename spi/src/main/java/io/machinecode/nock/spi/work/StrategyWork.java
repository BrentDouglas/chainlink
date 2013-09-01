package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.element.partition.Strategy;
import io.machinecode.nock.spi.inject.InjectionContext;

import javax.batch.api.partition.PartitionPlan;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface StrategyWork extends Strategy, Serializable {

    PartitionPlan getPartitionPlan(final InjectionContext context) throws Exception;
}
