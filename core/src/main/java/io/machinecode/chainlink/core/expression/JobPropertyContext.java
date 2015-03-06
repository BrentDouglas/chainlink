package io.machinecode.chainlink.core.expression;

import io.machinecode.chainlink.core.property.SystemPropertyLookup;
import io.machinecode.chainlink.spi.jsl.Property;

import java.util.Properties;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobPropertyContext extends PropertyContext {

    final PropertyResolver parameters;
    final SystemResolver system;

    public JobPropertyContext(final Properties parameters, final SystemPropertyLookup system) {
        super(new PropertyResolver(Expression.JOB_PROPERTIES, Expression.JOB_PROPERTIES_LENGTH, new Properties()));
        this.parameters = new PropertyResolver(Expression.JOB_PARAMETERS, Expression.JOB_PARAMETERS_LENGTH, parameters);
        this.system = new SystemResolver(system);
    }

    public void addProperty(final Property property) {
        this.properties.properties.put(property.getName(), property.getValue());
    }
}
