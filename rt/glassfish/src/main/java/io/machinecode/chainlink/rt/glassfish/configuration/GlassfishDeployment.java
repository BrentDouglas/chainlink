package io.machinecode.chainlink.rt.glassfish.configuration;

import io.machinecode.chainlink.core.configuration.op.Creator;
import io.machinecode.chainlink.spi.management.Mutable;
import io.machinecode.chainlink.spi.management.Op;
import io.machinecode.chainlink.core.schema.DeploymentSchema;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.JobOperatorWithNameExistsException;
import io.machinecode.chainlink.core.schema.MutableDeploymentSchema;
import io.machinecode.chainlink.core.schema.NoJobOperatorWithNameException;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Configured;
import org.jvnet.hk2.config.DuckTyped;
import org.jvnet.hk2.config.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Configured
public interface GlassfishDeployment extends ConfigBeanProxy, MutableDeploymentSchema<GlassfishDeclaration, GlassfishProperty, GlassfishJobOperator>, Hack<DeploymentSchema<?,?,?>> {

    @Attribute("name")
    String getName();

    void setName(final String name);

    @Attribute("ref")
    String getRef();

    void setRef(final String ref);

    @Element("configuration-loader")
    List<GlassfishDeclaration> getConfigurationLoader();

    void setConfigurationLoader(final List<GlassfishDeclaration> configurationLoader);

    @Element("job-operator")
    List<GlassfishJobOperator> getJobOperator();

    void setJobOperator(final List<GlassfishJobOperator> jobOperators);

    @DuckTyped
    List<GlassfishDeclaration> getConfigurationLoaders();

    @DuckTyped
    void setConfigurationLoaders(final List<GlassfishDeclaration> artifactLoaders);

    @DuckTyped
    List<GlassfishJobOperator> getJobOperators();

    @DuckTyped
    void setJobOperators(final List<GlassfishJobOperator> jobOperators);

    @DuckTyped
    GlassfishDeclaration getConfigurationLoader(final String name);

    @DuckTyped
    GlassfishJobOperator getJobOperator(final String name);

    @DuckTyped
    GlassfishJobOperator removeJobOperator(final String name) throws NoJobOperatorWithNameException;

    @DuckTyped
    void addJobOperator(final JobOperatorSchema<?,?> jobOperator) throws Exception;

    class Duck implements Mutable<DeploymentSchema<?,?,?>> {

        private final GlassfishDeployment to;

        public Duck(final GlassfishDeployment to) {
            this.to = to;
        }

        public static List<GlassfishDeclaration> getConfigurationLoaders(final GlassfishDeployment that) {
            return that.getConfigurationLoader();
        }

        public static void setConfigurationLoaders(final GlassfishDeployment that, final List<GlassfishDeclaration> artifactLoaders) {
            that.setConfigurationLoader(artifactLoaders);
        }

        public static List<GlassfishJobOperator> getJobOperators(final GlassfishDeployment that) {
            return that.getJobOperator();
        }

        public static void setJobOperators(final GlassfishDeployment that, final List<GlassfishJobOperator> jobOperators) {
            that.setJobOperator(jobOperators);
        }

        public static GlassfishDeclaration getConfigurationLoader(final GlassfishDeployment self, final String name) {
            for (final GlassfishDeclaration dep : self.getConfigurationLoaders()) {
                if (name.equals(dep.getName())) {
                    return dep;
                }
            }
            return null;
        }

        public static GlassfishJobOperator getJobOperator(final GlassfishDeployment self, final String name) {
            for (final GlassfishJobOperator dep : self.getJobOperators()) {
                if (name.equals(dep.getName())) {
                    return dep;
                }
            }
            return null;
        }

        public static GlassfishJobOperator removeJobOperator(final GlassfishDeployment self, final String name) throws NoJobOperatorWithNameException {
            final List<GlassfishJobOperator> jobOperators = new ArrayList<>(self.getJobOperators());
            final ListIterator<GlassfishJobOperator> it = jobOperators.listIterator();
            while (it.hasNext()) {
                final GlassfishJobOperator that = it.next();
                if (name.equals(that.getName())) {
                    it.remove();
                    self.setJobOperators(jobOperators);
                    return that;
                }
            }
            throw new NoJobOperatorWithNameException("No job operator with name " + name);
        }

        public static void addJobOperator(final GlassfishDeployment self, final JobOperatorSchema<?,?> jobOperator) throws Exception {
            if (getJobOperator(self, jobOperator.getName()) != null) {
                throw new JobOperatorWithNameExistsException("A job operator already exists with name " + jobOperator.getName());
            }
            final GlassfishJobOperator op = self.createChild(GlassfishJobOperator.class);
            op.accept(jobOperator);
            self.getJobOperators().add(op);
        }

        @Override
        public boolean willAccept(final DeploymentSchema<?,?,?> that) {
            return to.getName().equals(that.getName());
        }

        @Override
        public void accept(final DeploymentSchema<?,?,?> from, final Op... ops) throws Exception {
            to.setName(from.getName());
            to.setRef(from.getRef());
            to.setConfigurationLoaders(GlassfishTransmute.list(to.getConfigurationLoaders(), from.getConfigurationLoaders(), new Creator<GlassfishDeclaration>() {
                @Override
                public GlassfishDeclaration create() throws Exception {
                    return to.createChild(GlassfishDeclaration.class);
                }
            }, ops));
            to.setJobOperators(GlassfishTransmute.<JobOperatorSchema<?,?>, GlassfishJobOperator>list(to.getJobOperators(), from.getJobOperators(), new Creator<GlassfishJobOperator>() {
                @Override
                public GlassfishJobOperator create() throws Exception {
                    return to.createChild(GlassfishJobOperator.class);
                }
            }, ops));
        }
    }
}
