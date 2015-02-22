package io.machinecode.chainlink.rt.glassfish.schema;

import io.machinecode.chainlink.core.util.Creator;
import io.machinecode.chainlink.core.util.Mutable;
import io.machinecode.chainlink.core.util.Op;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.MutableJobOperatorSchema;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Configured;
import org.jvnet.hk2.config.DuckTyped;
import org.jvnet.hk2.config.Element;
import org.jvnet.hk2.config.TransactionFailure;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Configured(name = "job-operator")
public interface GlassfishJobOperator extends ConfigBeanProxy, MutableJobOperatorSchema<GlassfishDeclaration, GlassfishProperty>, Hack<JobOperatorSchema<?,?>> {

    @Attribute("name")
    String getName();

    void setName(final String name);

    @Attribute("ref")
    String getRef();

    void setRef(final String ref);

    @Element("class-loader")
    GlassfishDeclaration getClassLoader();

    void setClassLoader(final GlassfishDeclaration classLoader);

    @Element("transaction-manager")
    GlassfishDeclaration getTransactionManager();

    void setTransactionManager(final GlassfishDeclaration transactionManager);

    @Element("marshalling")
    GlassfishDeclaration getMarshalling();

    void setMarshalling(final GlassfishDeclaration marshalling);

    @Element("mbean-server")
    GlassfishDeclaration getMbeanServer();

    void setMbeanServer(final GlassfishDeclaration mBeanServer);

    @Element("repository")
    GlassfishDeclaration getRepository();

    void setRepository(final GlassfishDeclaration repository);

    @Element("registry")
    GlassfishDeclaration getRegistry();

    void setRegistry(final GlassfishDeclaration registry);

    @Element("transport")
    GlassfishDeclaration getTransport();

    void setTransport(final GlassfishDeclaration transport);

    @Element("executor")
    GlassfishDeclaration getExecutor();

    void setExecutor(final GlassfishDeclaration executor);

    @Element("artifact-loader")
    List<GlassfishDeclaration> getArtifactLoader();

    void setArtifactLoader(final List<GlassfishDeclaration> artifactLoaders);

    @Element("security")
    List<GlassfishDeclaration> getSecurity();

    void setSecurity(final List<GlassfishDeclaration> security);

    @Element("job-loader")
    List<GlassfishDeclaration> getJobLoader();

    void setJobLoader(final List<GlassfishDeclaration> jobLoaders);

    @Element("property")
    List<GlassfishProperty> getProperty();

    void setProperty(final List<GlassfishProperty> properties);

    @DuckTyped
    GlassfishDeclaration getMBeanServer();

    @DuckTyped
    void setMBeanServer(final GlassfishDeclaration mBeanServer);

    @DuckTyped
    List<GlassfishDeclaration> getArtifactLoaders();

    @DuckTyped
    void setArtifactLoaders(final List<GlassfishDeclaration> artifactLoaders);

    @DuckTyped
    List<GlassfishDeclaration> getSecurities();

    @DuckTyped
    void setSecurities(final List<GlassfishDeclaration> security);

    @DuckTyped
    List<GlassfishDeclaration> getJobLoaders();

    @DuckTyped
    void setJobLoaders(final List<GlassfishDeclaration> jobLoaders);

    @DuckTyped
    List<GlassfishProperty> getProperties();

    @DuckTyped
    void setProperties(final List<GlassfishProperty> properties);

    @DuckTyped
    void setProperty(final String name, final String value);

    class Duck implements Mutable<JobOperatorSchema<?,?>> {

        private final GlassfishJobOperator to;

        public Duck(final GlassfishJobOperator to) {
            this.to = to;
        }

        public static GlassfishDeclaration getMBeanServer(final GlassfishJobOperator that) {
            return that.getMbeanServer();
        }

        public static void setMBeanServer(final GlassfishJobOperator that, final GlassfishDeclaration mBeanServer) {
            that.setMbeanServer(mBeanServer);
        }

        public static List<GlassfishDeclaration> getArtifactLoaders(final GlassfishJobOperator that) {
            return that.getArtifactLoader();
        }

        public static void setArtifactLoaders(final GlassfishJobOperator that, final List<GlassfishDeclaration> artifactLoaders) {
            that.setArtifactLoader(artifactLoaders);
        }

        public static List<GlassfishDeclaration> getSecurities(final GlassfishJobOperator that) {
            return that.getSecurity();
        }

        public static void setSecurities(final GlassfishJobOperator that, final List<GlassfishDeclaration> security) {
            that.setSecurity(security);
        }

        public static List<GlassfishDeclaration> getJobLoaders(final GlassfishJobOperator that) {
            return that.getJobLoader();
        }

        public static void setJobLoaders(final GlassfishJobOperator that, final List<GlassfishDeclaration> jobLoaders) {
            that.setJobLoader(jobLoaders);
        }

        public static List<GlassfishProperty> getProperties(final GlassfishJobOperator that) {
            return that.getProperty();
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
        public boolean willAccept(final JobOperatorSchema<?,?> from) {
            return to.getName().equals(from.getName());
        }

        @Override
        public void accept(final JobOperatorSchema<?,?> from, final Op... ops) throws Exception {
            to.setName(from.getName());
            to.setRef(from.getRef());
            final Creator<GlassfishDeclaration> creator = new Creator<GlassfishDeclaration>() {
                @Override
                public GlassfishDeclaration create() throws Exception {
                    return to.createChild(GlassfishDeclaration.class);
                }
            };
            to.setArtifactLoaders(GlassfishTransmute.list(to.getArtifactLoaders(), from.getArtifactLoaders(), creator, ops));
            to.setSecurities(GlassfishTransmute.list(to.getSecurities(), from.getSecurities(), creator, ops));
            to.setJobLoaders(GlassfishTransmute.list(to.getJobLoaders(), from.getJobLoaders(), creator, ops));

            to.setClassLoader(GlassfishTransmute.item(to.getClassLoader(), from.getClassLoader(), creator, ops));
            to.setTransactionManager(GlassfishTransmute.item(to.getTransactionManager(), from.getTransactionManager(), creator, ops));
            to.setMarshalling(GlassfishTransmute.item(to.getMarshalling(), from.getMarshalling(), creator, ops));
            to.setMBeanServer(GlassfishTransmute.item(to.getMBeanServer(), from.getMBeanServer(), creator, ops));
            to.setRepository(GlassfishTransmute.item(to.getRepository(), from.getRepository(), creator, ops));
            to.setRegistry(GlassfishTransmute.item(to.getRegistry(), from.getRegistry(), creator, ops));
            to.setTransport(GlassfishTransmute.item(to.getTransport(), from.getTransport(), creator, ops));
            to.setExecutor(GlassfishTransmute.item(to.getExecutor(), from.getExecutor(), creator, ops));

            to.setProperties(GlassfishTransmute.list(to.getProperties(), from.getProperties(), new Creator<GlassfishProperty>() {
                @Override
                public GlassfishProperty create() throws Exception {
                    return to.createChild(GlassfishProperty.class);
                }
            }, ops));
        }
    }
}
