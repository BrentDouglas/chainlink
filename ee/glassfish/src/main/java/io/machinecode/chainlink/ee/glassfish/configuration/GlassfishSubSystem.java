package io.machinecode.chainlink.ee.glassfish.configuration;

import io.machinecode.chainlink.core.configuration.def.DeploymentDef;
import io.machinecode.chainlink.core.configuration.def.JobOperatorDef;
import io.machinecode.chainlink.core.configuration.def.SubSystemDef;
import io.machinecode.chainlink.core.configuration.op.Creator;
import io.machinecode.chainlink.core.configuration.op.Mutable;
import io.machinecode.chainlink.core.configuration.op.Op;
import org.glassfish.api.admin.config.ConfigExtension;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Configured;
import org.jvnet.hk2.config.DuckTyped;
import org.jvnet.hk2.config.Element;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Configured(name = "chainlink")
public interface GlassfishSubSystem extends ConfigBeanProxy, ConfigExtension, SubSystemDef<GlassfishDeployment, GlassfishDeclaration, GlassfishProperty, GlassfishJobOperator>, Hack<SubSystemDef<?,?,?,?>> {

    @Attribute("ref")
    String getRef();

    void setRef(final String ref);

    @Element("artifact-loader")
    List<GlassfishDeclaration> getArtifactLoader();

    void setArtifactLoader(final List<GlassfishDeclaration> artifactLoaders);

    @Element("job-operator")
    List<GlassfishJobOperator> getJobOperator();

    void setJobOperator(final List<GlassfishJobOperator> jobOperators);

    @Element("deployment")
    List<GlassfishDeployment> getDeployment();

    void setDeployment(final List<GlassfishDeployment> deployments);

    @DuckTyped
    List<GlassfishDeclaration> getArtifactLoaders();

    @DuckTyped
    void setArtifactLoaders(final List<GlassfishDeclaration> artifactLoaders);

    @DuckTyped
    List<GlassfishJobOperator> getJobOperators();

    @DuckTyped
    void setJobOperators(final List<GlassfishJobOperator> jobOperators);

    @DuckTyped
    List<GlassfishDeployment> getDeployments();

    @DuckTyped
    void setDeployments(final List<GlassfishDeployment> deployments);

    @DuckTyped
    GlassfishDeployment getDeployment(final String name);

    @DuckTyped
    GlassfishDeclaration getArtifactLoader(final String name);

    @DuckTyped
    GlassfishJobOperator getJobOperator(final String name);

    class Duck implements Mutable<SubSystemDef<?,?,?,?>> {

        private final GlassfishSubSystem to;

        public Duck(final GlassfishSubSystem to) {
            this.to = to;
        }

        public static List<GlassfishDeclaration> getArtifactLoaders(final GlassfishSubSystem that) {
            return that.getArtifactLoader();
        }

        public static void setArtifactLoaders(final GlassfishSubSystem that, final List<GlassfishDeclaration> artifactLoaders) {
            that.setArtifactLoader(artifactLoaders);
        }

        public static List<GlassfishJobOperator> getJobOperators(final GlassfishSubSystem that) {
            return that.getJobOperator();
        }

        public static void setJobOperators(final GlassfishSubSystem that, final List<GlassfishJobOperator> jobOperators) {
            that.setJobOperator(jobOperators);
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

        public static GlassfishDeclaration getArtifactLoader(final GlassfishSubSystem self, final String name) {
            for (final GlassfishDeclaration dep : self.getArtifactLoaders()) {
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

        @Override
        public boolean willAccept(final SubSystemDef<?,?,?,?> that) {
            return true;
        }

        @Override
        public void accept(final SubSystemDef<?,?,?,?> from, final Op... ops) throws Exception {
            to.setRef(from.getRef());
            to.setArtifactLoaders(GlassfishTransmute.list(to.getArtifactLoaders(), from.getArtifactLoaders(), new Creator<GlassfishDeclaration>() {
                @Override
                public GlassfishDeclaration create() throws Exception {
                    return to.createChild(GlassfishDeclaration.class);
                }
            }, ops));
            to.setDeployments(GlassfishTransmute.<DeploymentDef<?,?,?>,GlassfishDeployment>list(to.getDeployments(), from.getDeployments(), new Creator<GlassfishDeployment>() {
                @Override
                public GlassfishDeployment create() throws Exception {
                    return to.createChild(GlassfishDeployment.class);
                }
            }, ops));
            to.setJobOperators(GlassfishTransmute.<JobOperatorDef<?,?>,GlassfishJobOperator>list(to.getJobOperators(), from.getJobOperators(), new Creator<GlassfishJobOperator>() {
                @Override
                public GlassfishJobOperator create() throws Exception {
                    return to.createChild(GlassfishJobOperator.class);
                }
            }, ops));
        }
    }

}
