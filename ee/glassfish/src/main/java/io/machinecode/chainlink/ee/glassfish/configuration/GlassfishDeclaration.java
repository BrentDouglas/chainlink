package io.machinecode.chainlink.ee.glassfish.configuration;

import io.machinecode.chainlink.core.configuration.def.DeclarationDef;
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
public interface GlassfishDeclaration extends ConfigBeanProxy, DeclarationDef, Hack<DeclarationDef> {

    @Attribute("name")
    String getName();

    void setName(final String name);

    @Attribute("ref")
    String getRef();

    void setRef(final String ref);

    class Duck implements Mutable<DeclarationDef> {

        private final GlassfishDeclaration to;

        public Duck(final GlassfishDeclaration to) {
            this.to = to;
        }

        @Override
        public boolean willAccept(final DeclarationDef that) {
            return to.getName().equals(that.getName());
        }

        @Override
        public void accept(final DeclarationDef from, final Op... ops) throws Exception {
            to.setName(from.getName());
            to.setRef(from.getRef());
        }
    }
}
