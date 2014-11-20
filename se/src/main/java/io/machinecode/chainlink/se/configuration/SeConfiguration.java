package io.machinecode.chainlink.se.configuration;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl;
import io.machinecode.chainlink.se.configuration.xml.XmlClassRef;
import io.machinecode.chainlink.se.configuration.xml.XmlConfiguration;
import io.machinecode.chainlink.se.configuration.xml.XmlProperty;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class SeConfiguration extends ConfigurationImpl {

    public SeConfiguration(final Builder builder) throws Exception {
        super(builder, new SeConfigurationDefaults());
    }

    public SeConfiguration(final XmlConfiguration xml) throws Exception {
        super(toBuilder(xml), new SeConfigurationDefaults());
    }

    public static Builder toBuilder(final XmlConfiguration xml) {
        final Builder builder = new Builder();
        for (final XmlProperty property : xml.getProperties()) {
            builder.setProperty(property.getKey(), property.getValue());
        }
        return builder.setClassLoaderFactoryFqcn(xml.getClassLoaderFactory().getClazz())
                .setTransactionManagerFactoryFqcn(xml.getTransactionManagerFactory().getClazz())
                .setJobLoaderFactoriesFqcns(_fqcns(xml.getJobLoaderFactories()))
                .setArtifactLoaderFactoriesFqcns(_fqcns(xml.getArtifactLoaderFactories()))
                .setInjectorFactoriesFqcns(_fqcns(xml.getInjectorFactories()))
                .setSecurityCheckFactoriesFqcns(_fqcns(xml.getSecurityCheckFactories()))
                .setMarshallingProviderFactoryFqcn(xml.getMarshallerFactory().getClazz())
                .setExecutionRepositoryFactoryFqcn(xml.getMarshallerFactory().getClazz())
                .setMBeanServerFactoryFqcn(xml.getmBeanServerFactory().getClazz())
                .setRegistryFactoryFqcn(xml.getRegistryFactory().getClazz())
                .setExecutorFactoryFqcn(xml.getExecutorFactory().getClazz())
                .setWorkerFactoryFqcn(xml.getWorkerFactory().getClazz());
    }

    protected static String[] _fqcns(final List<XmlClassRef> refs) {
        if (refs == null) {
            return null;
        }
        final String[] ret = new String[refs.size()];
        for (int i = 0; i < refs.size(); ++i) {
            ret[i] = refs.get(i).getClazz();
        }
        return ret;
    }

    public static class Builder extends _Builder<Builder> {
        @Override
        public SeConfiguration build() throws Exception {
            return new SeConfiguration(this);
        }
    }
}
