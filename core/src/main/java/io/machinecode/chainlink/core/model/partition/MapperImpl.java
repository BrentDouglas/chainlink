package io.machinecode.chainlink.core.model.partition;

import io.machinecode.chainlink.core.loader.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.model.PropertiesImpl;
import io.machinecode.chainlink.core.model.PropertyReferenceImpl;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.partition.Mapper;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.StrategyWork;
import org.jboss.logging.Logger;

import javax.batch.api.partition.PartitionMapper;
import javax.batch.api.partition.PartitionPlan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperImpl extends PropertyReferenceImpl<PartitionMapper> implements Mapper, StrategyWork {

    private static final Logger log = Logger.getLogger(MapperImpl.class);

    public MapperImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    @Override
    public PartitionPlan getPartitionPlan(final Executor executor, final ExecutionContext context) throws Exception {
        log.debugf(Messages.get("CHAINLINK-022000.mapper.map.partitions"), context.getJobExecutionId(), ref.ref());
        return mapPartitions(executor, context);
    }

    public PartitionPlan mapPartitions(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(PartitionMapper.class, injectionContext, context).mapPartitions();
        } finally {
            provider.setInjectables(null);
        }
    }
}
