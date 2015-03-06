package io.machinecode.chainlink.rt.glassfish.schema;

import io.machinecode.chainlink.core.util.Creator;
import io.machinecode.chainlink.core.util.Mutable;
import io.machinecode.chainlink.core.util.Op;
import io.machinecode.chainlink.core.schema.DeploymentSchema;
import io.machinecode.chainlink.core.schema.DeploymentWithNameExistsException;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.JobOperatorWithNameExistsException;
import io.machinecode.chainlink.core.schema.MutableSubSystemSchema;
import io.machinecode.chainlink.core.schema.NoDeploymentWithNameException;
import io.machinecode.chainlink.core.schema.NoJobOperatorWithNameException;
import io.machinecode.chainlink.core.schema.SubSystemSchema;
import org.glassfish.api.admin.config.ConfigExtension;
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
@Configured(name = "chainlink")
public interface GlassfishSubSystem extends ConfigBeanProxy, ConfigExtension, MutableSubSystemSchema<GlassfishDeployment, GlassfishDeclaration, GlassfishProperty, GlassfishJobOperator>, Hack<SubSystemSchema<?,?,?,?>> {

    @Attribute("ref")
    String getRef();

    void setRef(final String ref);

    @Element("configuration-loader")
    List<GlassfishDeclaration> getConfigurationLoader();

    void setConfigurationLoader(final List<GlassfishDeclaration> configurationLoaders);

    @Element("job-operator")
    List<GlassfishJobOperator> getJobOperator();

    void setJobOperator(final List<GlassfishJobOperator> jobOperators);

    @Element("deployment")
    List<GlassfishDeployment> getDeployment();

    void setDeployment(final List<GlassfishDeployment> deployments);

    @DuckTyped
    List<GlassfishDeclaration> getConfigurationLoaders();

    @DuckTyped
    void setConfigurationLoaders(final List<GlassfishDeclaration> configurationLoaders);

    @DuckTyped
    List<GlassfishJobOperator> getJobOperators();

    @DuckTyped
    void setJobOperators(final List<GlassfishJobOperator> jobOperators);

    @DuckTyped
    void setProperties(final List<GlassfishProperty> properties);

    @DuckTyped
    List<GlassfishProperty> getProperties();

    @DuckTyped
    List<GlassfishDeployment> getDeployments();

    @DuckTyped
    void setDeployments(final List<GlassfishDeployment> deployments);

    @DuckTyped
    GlassfishJobOperator getJobOperator(final String name);

    @DuckTyped
    GlassfishJobOperator removeJobOperator(final String name) throws NoJobOperatorWithNameException;

    @DuckTyped
    void addJobOperator(final JobOperatorSchema<?,?> jobOperator) throws Exception;

    @DuckTyped
    GlassfishDeployment getDeployment(final String name);

    @DuckTyped
    GlassfishDeployment removeDeployment(final String name) throws NoDeploymentWithNameException;

    @DuckTyped
    void addDeployment(final DeploymentSchema<?,?,?> deployment) throws Exception;

    class Duck implements Mutable<SubSystemSchema<?,?,?,?>>, MutableSubSystemSchema<GlassfishDeployment, GlassfishDeclaration, GlassfishProperty, GlassfishJobOperator> {

        private final GlassfishSubSystem to;

        public Duck(final GlassfishSubSystem to) {
            this.to = to;
        }

        public static List<GlassfishDeclaration> getConfigurationLoaders(final GlassfishSubSystem that) {
            return that.getConfigurationLoader();
        }

        public static void setConfigurationLoaders(final GlassfishSubSystem that, final List<GlassfishDeclaration> configurationLoaders) {
            that.setConfigurationLoader(configurationLoaders);
        }

        public static List<GlassfishJobOperator> getJobOperators(final GlassfishSubSystem that) {
            return that.getJobOperator();
        }

        public static void setJobOperators(final GlassfishSubSystem that, final List<GlassfishJobOperator> jobOperators) {
            that.setJobOperator(jobOperators);
        }

        public static void setProperties(final GlassfishSubSystem that, final List<GlassfishProperty> properties) {
            that.setProperties(properties);
        }

        public static List<GlassfishProperty> getProperties(final GlassfishSubSystem that) {
            return that.getProperties();
        }

        public static List<GlassfishDeployment> getDeployments(final GlassfishSubSystem that) {
            return that.getDeployment();
        }

        public static void setDeployments(final GlassfishSubSystem that, final List<GlassfishDeployment> deployments) {
            that.setDeployment(deployments);
        }

        public static GlassfishDeployment getDeployment(final GlassfishSubSystem self, final String name) {
            for (final GlassfishDeployment dep : self.getDeployments()) {
                if (name.equals(dep.getName())) {
                    return dep;
                }
            }
            return null;
        }

        public static GlassfishJobOperator getJobOperator(final GlassfishSubSystem self, final String name) {
            for (final GlassfishJobOperator dep : self.getJobOperators()) {
                if (name.equals(dep.getName())) {
                    return dep;
                }
            }
            return null;
        }

        public static GlassfishJobOperator removeJobOperator(final GlassfishSubSystem self, final String name) throws NoJobOperatorWithNameException {
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

        public static GlassfishDeployment removeDeployment(final GlassfishSubSystem self, final String name) throws NoDeploymentWithNameException {
            final List<GlassfishDeployment> deployments = new ArrayList<>(self.getDeployments());
            final ListIterator<GlassfishDeployment> it = deployments.listIterator();
            while (it.hasNext()) {
                final GlassfishDeployment that = it.next();
                if (name.equals(that.getName())) {
                    it.remove();
                    self.setDeployments(deployments);
                    return that;
                }
            }
            throw new NoDeploymentWithNameException("No deployment with name " + name); //TODO Message
        }

        public static void addJobOperator(final GlassfishSubSystem self, final JobOperatorSchema<?,?> jobOperator) throws Exception {
            if (getJobOperator(self, jobOperator.getName()) != null) {
                throw new JobOperatorWithNameExistsException("A job operator already exists with name " + jobOperator.getName());
            }
            final GlassfishJobOperator op = self.createChild(GlassfishJobOperator.class);
            op.accept(jobOperator);
            self.getJobOperators().add(op);
        }

        public static void addDeployment(final GlassfishSubSystem self, final DeploymentSchema<?,?,?> deployment) throws Exception {
            if (getDeployment(self, deployment.getName()) != null) {
                throw new DeploymentWithNameExistsException("A deployment already exists with name " + deployment.getName());
            }
            final GlassfishDeployment op = self.createChild(GlassfishDeployment.class);
            op.accept(deployment);
            self.getDeployments().add(op);
        }

        @Override
        public boolean willAccept(final SubSystemSchema<?,?,?,?> that) {
            return true;
        }

        @Override
        public void accept(final SubSystemSchema<?,?,?,?> from, final Op... ops) throws Exception {
            to.setRef(from.getRef());
            to.setConfigurationLoaders(GlassfishTransmute.list(to.getConfigurationLoaders(), from.getConfigurationLoaders(), new Creator<GlassfishDeclaration>() {
                @Override
                public GlassfishDeclaration create() throws Exception {
                    return to.createChild(GlassfishDeclaration.class);
                }
            }, ops));
            to.setDeployments(GlassfishTransmute.<DeploymentSchema<?,?,?>,GlassfishDeployment>list(to.getDeployments(), from.getDeployments(), new Creator<GlassfishDeployment>() {
                @Override
                public GlassfishDeployment create() throws Exception {
                    return to.createChild(GlassfishDeployment.class);
                }
            }, ops));
            to.setJobOperators(GlassfishTransmute.<JobOperatorSchema<?,?>,GlassfishJobOperator>list(to.getJobOperators(), from.getJobOperators(), new Creator<GlassfishJobOperator>() {
                @Override
                public GlassfishJobOperator create() throws Exception {
                    return to.createChild(GlassfishJobOperator.class);
                }
            }, ops));
        }

        @Override
        public String getRef() {
            return to.getRef();
        }

        @Override
        public void setRef(final String ref) {
            to.setRef(ref);
        }

        @Override
        public List<GlassfishDeclaration> getConfigurationLoaders() {
            return to.getConfigurationLoaders();
        }

        @Override
        public void setConfigurationLoaders(final List<GlassfishDeclaration> configurationLoaders) {
            to.setConfigurationLoaders(configurationLoaders);
        }

        @Override
        public List<GlassfishJobOperator> getJobOperators() {
            return to.getJobOperators();
        }

        @Override
        public List<GlassfishProperty> getProperties() {
            return to.getProperties();
        }

        @Override
        public void setJobOperators(final List<GlassfishJobOperator> jobOperators) {
            to.setJobOperators(jobOperators);
        }

        @Override
        public void setProperties(final List<GlassfishProperty> properties) {
            to.setProperties(properties);
        }

        @Override
        public List<GlassfishDeployment> getDeployments() {
            return to.getDeployments();
        }

        @Override
        public void setDeployments(final List<GlassfishDeployment> deployments) {
            to.setDeployments(deployments);
        }

        @Override
        public GlassfishJobOperator getJobOperator(final String name) {
            return to.getJobOperator(name);
        }

        @Override
        public GlassfishJobOperator removeJobOperator(final String name) throws NoJobOperatorWithNameException {
            return to.removeJobOperator(name);
        }

        @Override
        public void addJobOperator(final JobOperatorSchema<?,?> jobOperator) throws Exception {
            to.addJobOperator(jobOperator);
        }

        @Override
        public GlassfishDeployment getDeployment(final String name) {
            return to.getDeployment(name);
        }

        @Override
        public GlassfishDeployment removeDeployment(final String name) throws NoDeploymentWithNameException {
            return to.removeDeployment(name);
        }

        @Override
        public void addDeployment(final DeploymentSchema<?,?,?> deployment) throws Exception {
            to.addDeployment(deployment);
        }
    }

}
