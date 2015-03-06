package io.machinecode.chainlink.core.expression;

import java.util.Properties;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class PartitionPropertyContext extends PropertyContext {

    public PartitionPropertyContext(final Properties properties) {
        super(new PropertyResolver(Expression.PARTITION_PLAN, Expression.PARTITION_PLAN_LENGTH, properties));
    }
}
