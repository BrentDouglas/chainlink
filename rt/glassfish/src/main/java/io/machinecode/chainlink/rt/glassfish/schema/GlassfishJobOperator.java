package io.machinecode.chainlink.rt.glassfish.schema;

import io.machinecode.chainlink.core.schema.xml.XmlSchema;
import io.machinecode.chainlink.core.util.Creator;
import io.machinecode.chainlink.core.util.Mutable;
import io.machinecode.chainlink.core.util.Op;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.MutableJobOperatorSchema;
import io.machinecode.chainlink.core.util.Strings;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Configured;
import org.jvnet.hk2.config.DuckTyped;
import org.jvnet.hk2.config.Element;
import org.jvnet.hk2.config.TransactionFailure;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Configured(name = "job-operator")
public interface GlassfishJobOperator extends ConfigBeanProxy, MutableJobOperatorSchema<GlassfishProperty>, Hack<JobOperatorSchema<?>> {

    @Attribute("name")
    String getName();

    void setName(final String name);

    @Attribute("ref")
    String getRef();

    void setRef(final String ref);

    @Attribute("class-loader")
    String getClassLoader();

    void setClassLoader(final String classLoader);

    @Attribute("transaction-manager")
    String getTransactionManager();

    void setTransactionManager(final String transactionManager);

    @Attribute("marshalling")
    String getMarshalling();

    void setMarshalling(final String marshalling);

    @Attribute("mbean-server")
    String getMbeanServer();

    void setMbeanServer(final String mBeanServer);

    @Attribute("repository")
    String getRepository();

    void setRepository(final String repository);

    @Attribute("registry")
    String getRegistry();

    void setRegistry(final String registry);

    @Attribute("transport")
    String getTransport();

    void setTransport(final String transport);

    @Attribute("executor")
    String getExecutor();

    void setExecutor(final String executor);

    @Attribute("artifact-loaders")
    String getArtifactLoadersString();

    @Attribute("artifact-loaders")
    void setArtifactLoadersString(final String artifactLoaders);

    @Attribute("securities")
    String getSecuritiesString();

    @Attribute("securities")
    void setSecuritiesString(final String security);

    @Attribute("job-loaders")
    String getJobLoadersString();

    @Attribute("job-loaders")
    void setJobLoadersString(final String jobLoaders);

    @Element("property")
    List<GlassfishProperty> getProperty();

    void setProperty(final List<GlassfishProperty> properties);

    @DuckTyped
    String getMBeanServer();

    @DuckTyped
    void setMBeanServer(final String mBeanServer);

    @DuckTyped
    List<GlassfishProperty> getProperties();

    @DuckTyped
    List<String> getArtifactLoaders();

    @DuckTyped
    void setArtifactLoaders(final List<String> artifactLoaders);

    @DuckTyped
    List<String> getSecurities();

    @DuckTyped
    void setSecurities(final List<String> security);

    @DuckTyped
    List<String> getJobLoaders();

    @DuckTyped
    void setJobLoaders(final List<String> jobLoaders);

    @DuckTyped
    void setProperties(final List<GlassfishProperty> properties);

    @DuckTyped
    void setProperty(final String name, final String value);

    class Duck implements Mutable<JobOperatorSchema<?>> {

        private final GlassfishJobOperator to;

        public Duck(final GlassfishJobOperator to) {
            this.to = to;
        }

        public static String getMBeanServer(final GlassfishJobOperator that) {
            return that.getMbeanServer();
        }

        public static void setMBeanServer(final GlassfishJobOperator that, final String mBeanServer) {
            that.setMbeanServer(mBeanServer);
        }

        public static List<GlassfishProperty> getProperties(final GlassfishJobOperator that) {
            return that.getProperty();
        }

        public static List<String> getArtifactLoaders(final GlassfishJobOperator that) {
            final String value = that.getArtifactLoadersString();
            return value == null || value.isEmpty() ? Collections.<String>emptyList() : Strings.split(XmlSchema.XML_LIST_DELIMITER, value);
        }

        public static void setArtifactLoaders(final GlassfishJobOperator that, final List<String> value) {
            that.setArtifactLoadersString(value == null || value.isEmpty() ? null : Strings.join(' ', value));
        }

        public static List<String> getSecurities(final GlassfishJobOperator that) {
            final String value = that.getSecuritiesString();
            return value == null || value.isEmpty() ? Collections.<String>emptyList() : Strings.split(XmlSchema.XML_LIST_DELIMITER, value);
        }

        public static void setSecurities(final GlassfishJobOperator that, final List<String> value) {
            that.setSecuritiesString(value == null || value.isEmpty() ? null : Strings.join(' ', value));
        }

        public static List<String> getJobLoaders(final GlassfishJobOperator that) {
            final String value = that.getJobLoadersString();
            return value == null || value.isEmpty() ? Collections.<String>emptyList() : Strings.split(XmlSchema.XML_LIST_DELIMITER, value);
        }

        public static void setJobLoaders(final GlassfishJobOperator that, final List<String> value) {
            that.setJobLoadersString(value == null || value.isEmpty() ? null : Strings.join(' ', value));
        }

        public static void setProperties(final GlassfishJobOperator that, final List<GlassfishProperty> properties) {
            that.setProperty(properties);
        }

        public static void setProperty(final GlassfishJobOperator that, final String name, final String value) {
            for (final GlassfishProperty property : that.getProperties()) {
                if (property.getName().equals(name)) {
                    property.setValue(value);
                    return;
                }
            }
            final GlassfishProperty property;
            try {
                property = that.createChild(GlassfishProperty.class);
            } catch (final TransactionFailure e) {
                throw new RuntimeException(e);
            }
            property.setName(name);
            property.setValue(value);
            that.getProperties().add(property);
        }

        @Override
        public boolean willAccept(final JobOperatorSchema<?> from) {
            return to.getName().equals(from.getName());
        }

        @Override
        public void accept(final JobOperatorSchema<?> from, final Op... ops) throws Exception {
            to.setName(from.getName());
            to.setRef(from.getRef());
            to.setArtifactLoaders(from.getArtifactLoaders());
            to.setSecurities(from.getSecurities());
            to.setJobLoaders(from.getJobLoaders());

            to.setClassLoader(from.getClassLoader());
            to.setTransactionManager(from.getTransactionManager());
            to.setMarshalling(from.getMarshalling());
            to.setMBeanServer(from.getMBeanServer());
            to.setRepository(from.getRepository());
            to.setRegistry(from.getRegistry());
            to.setTransport(from.getTransport());
            to.setExecutor(from.getExecutor());

            to.setProperties(GlassfishTransmute.list(to.getProperties(), from.getProperties(), new Creator<GlassfishProperty>() {
                @Override
                public GlassfishProperty create() throws Exception {
                    return to.createChild(GlassfishProperty.class);
                }
            }, ops));
        }
    }
}
