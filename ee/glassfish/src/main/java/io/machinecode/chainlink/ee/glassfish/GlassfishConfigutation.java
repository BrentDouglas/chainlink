package io.machinecode.chainlink.ee.glassfish;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl;
import io.machinecode.chainlink.core.configuration.xml.XmlConfiguration;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class GlassfishConfigutation extends ConfigurationImpl {

    public GlassfishConfigutation(final Builder builder) throws Exception {
        super(builder);
    }

    public static Builder xmlToBuilder(final XmlConfiguration xml) {
        return configureBuilder(new Builder(), xml);
    }


    public static class Builder extends _Builder<Builder> {
        @Override
        public GlassfishConfigutation build() throws Exception {
            return new GlassfishConfigutation(this);
        }
    }
}
