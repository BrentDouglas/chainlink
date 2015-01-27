package io.machinecode.chainlink.core.jsl.impl.partition;

import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.util.PropertiesConverter;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.jsl.partition.Plan;
import org.jboss.logging.Logger;

import javax.batch.api.partition.PartitionPlan;
import javax.batch.operations.BatchRuntimeException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PlanImpl implements Plan, StrategyWork, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(PlanImpl.class);

    private final String partitions;
    private final String threads;
    private final List<PropertiesImpl> properties;

    public PlanImpl(final String partitions, final String threads, final List<PropertiesImpl> properties) {
        this.partitions = partitions;
        this.threads = threads;
        this.properties = properties;
    }

    @Override
    public String getPartitions() {
        return this.partitions;
    }

    @Override
    public String getThreads() {
        return this.threads;
    }

    @Override
    public List<PropertiesImpl> getProperties() {
        return this.properties;
    }

    @Override
    public PartitionPlan getPartitionPlan(final Configuration configuration, final ExecutionContext context) {
        int threads;
        try {
            threads = Integer.parseInt(this.threads);
        } catch (final NumberFormatException e) {
            throw new BatchRuntimeException(Messages.format("CHAINLINK-012000.plan.threads.not.integer", context, this.threads), e);
        }
        int partitions;
        try {
            partitions = Integer.parseInt(this.partitions);
        } catch (final NumberFormatException e) {
            throw new BatchRuntimeException(Messages.format("CHAINLINK-012001.plan.partitions.not.integer", context, this.partitions), e);
        }
        final Properties[] properties = new Properties[partitions];
        for (final PropertiesImpl property : this.properties) {
            if (property.getPartition() == null) {
                throw new BatchRuntimeException(Messages.format("CHAINLINK-012002.plan.property.partition.null", context));
            }
            final int partition;
            try {
                partition = Integer.parseInt(property.getPartition());
            } catch (final NumberFormatException e) {
                throw new BatchRuntimeException(Messages.format("CHAINLINK-012003.plan.property.partition.not.integer", context, property.getPartition()), e);
            }
            if (partition >= partitions) {
                throw new BatchRuntimeException(Messages.format("CHAINLINK-012004.plan.property.partition.too.large", context, partition, partitions));
            }
            properties[partition] = PropertiesConverter.convert(property);
        }
        return new PartitionPlanImpl(
                partitions,
                threads,
                properties
        );
    }

    private static class PartitionPlanImpl implements PartitionPlan {
        private final int partitions;
        private final int threads;
        private final Properties[] properties;

        public PartitionPlanImpl(final int partitions, final int threads, final Properties[] properties) {
            this.partitions = partitions;
            this.threads = threads;
            this.properties = properties;
        }

        @Override
        public boolean getPartitionsOverride() {
            return false;
        }

        @Override
        public void setPartitionsOverride(final boolean override) {
            //
        }

        @Override
        public int getPartitions() {
            return this.partitions;
        }

        @Override
        public void setPartitions(final int partitions) {
            //
        }

        @Override
        public int getThreads() {
            return this.threads;
        }

        @Override
        public void setThreads(final int threads) {
            //
        }

        @Override
        public Properties[] getPartitionProperties() {
            return this.properties;
        }

        @Override
        public void setPartitionProperties(final Properties[] properties) {
            //
        }
    }
}
