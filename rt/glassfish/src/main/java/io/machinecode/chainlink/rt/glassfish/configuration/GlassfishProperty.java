package io.machinecode.chainlink.rt.glassfish.configuration;

import io.machinecode.chainlink.spi.management.Mutable;
import io.machinecode.chainlink.spi.management.Op;
import io.machinecode.chainlink.core.schema.MutablePropertySchema;
import io.machinecode.chainlink.core.schema.PropertySchema;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Configured;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Configured
public interface GlassfishProperty extends ConfigBeanProxy, MutablePropertySchema, Hack<PropertySchema> {

    @Attribute("name")
    String getName();

    void setName(final String name);

    @Attribute("value")
    String getValue();

    void setValue(final String value);

    class Duck implements Mutable<PropertySchema> {

        private final GlassfishProperty to;

        public Duck(final GlassfishProperty to) {
            this.to = to;
        }

        @Override
        public boolean willAccept(final PropertySchema from) {
            return to.getName().equals(from.getName());
        }

        @Override
        public void accept(final PropertySchema from, final Op... ops) {
            to.setName(from.getName());
            to.setValue(from.getValue());
        }
    }
}
