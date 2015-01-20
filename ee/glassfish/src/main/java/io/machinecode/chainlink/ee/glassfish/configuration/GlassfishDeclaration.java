package io.machinecode.chainlink.ee.glassfish.configuration;

import io.machinecode.chainlink.spi.management.Mutable;
import io.machinecode.chainlink.spi.management.Op;
import io.machinecode.chainlink.spi.schema.DeclarationSchema;
import io.machinecode.chainlink.spi.schema.MutableDeclarationSchema;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Configured;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Configured
public interface GlassfishDeclaration extends ConfigBeanProxy, MutableDeclarationSchema, Hack<DeclarationSchema> {

    @Attribute("name")
    String getName();

    void setName(final String name);

    @Attribute("ref")
    String getRef();

    void setRef(final String ref);

    class Duck implements Mutable<DeclarationSchema> {

        private final GlassfishDeclaration to;

        public Duck(final GlassfishDeclaration to) {
            this.to = to;
        }

        @Override
        public boolean willAccept(final DeclarationSchema that) {
            return to.getName().equals(that.getName());
        }

        @Override
        public void accept(final DeclarationSchema from, final Op... ops) throws Exception {
            to.setName(from.getName());
            to.setRef(from.getRef());
        }
    }
}
