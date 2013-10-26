package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.partition.Strategy;
import io.machinecode.nock.spi.transport.Transport;

import javax.batch.api.partition.PartitionPlan;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface StrategyWork extends Strategy, Serializable {

    PartitionPlan getPartitionPlan(final Transport transport, final Context context) throws Exception;
}
