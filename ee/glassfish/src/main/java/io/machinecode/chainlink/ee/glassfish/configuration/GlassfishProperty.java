package io.machinecode.chainlink.ee.glassfish.configuration;

import io.machinecode.chainlink.core.configuration.def.PropertyDef;
import io.machinecode.chainlink.core.configuration.op.Mutable;
import io.machinecode.chainlink.core.configuration.op.Op;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Configured;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Configured
public interface GlassfishProperty extends ConfigBeanProxy, PropertyDef, Hack<PropertyDef> {

    @Attribute("name")
    String getName();

    void setName(final String name);

    @Attribute("value")
    String getValue();

    void setValue(final String value);

    class Duck implements Mutable<PropertyDef> {

        private final GlassfishProperty to;

        public Duck(final GlassfishProperty to) {
            this.to = to;
        }

        @Override
        public boolean willAccept(final PropertyDef from) {
            return to.getName().equals(from.getName());
        }

        @Override
        public void accept(final PropertyDef from, final Op... ops) {
            to.setName(from.getName());
            to.setValue(from.getValue());
        }
    }
}
