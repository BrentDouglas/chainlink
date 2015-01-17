package io.machinecode.chainlink.ee.glassfish.configuration;

import io.machinecode.chainlink.core.configuration.def.DeploymentDef;
import io.machinecode.chainlink.core.configuration.def.JobOperatorDef;
import io.machinecode.chainlink.core.configuration.op.Creator;
import io.machinecode.chainlink.core.configuration.op.Mutable;
import io.machinecode.chainlink.core.configuration.op.Op;
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
@Configured
public interface GlassfishDeployment extends ConfigBeanProxy, DeploymentDef<GlassfishDeclaration, GlassfishProperty, GlassfishJobOperator>, Hack<DeploymentDef<?,?,?>> {

    @Attribute("name")
    String getName();

    void setName(final String name);

    @Attribute("ref")
    String getRef();

    void setRef(final String ref);

    @Element("artifact-loader")
    List<GlassfishDeclaration> getArtifactLoader();

    void setArtifactLoader(final List<GlassfishDeclaration> artifactLoaders);

    @Element("job-operator")
    List<GlassfishJobOperator> getJobOperator();

    void setJobOperator(final List<GlassfishJobOperator> jobOperators);

    @DuckTyped
    List<GlassfishDeclaration> getArtifactLoaders();

    @DuckTyped
    void setArtifactLoaders(final List<GlassfishDeclaration> artifactLoaders);

    @DuckTyped
    List<GlassfishJobOperator> getJobOperators();

    @DuckTyped
    void setJobOperators(final List<GlassfishJobOperator> jobOperators);

    @DuckTyped
    GlassfishDeclaration getArtifactLoader(final String name);

    @DuckTyped
    GlassfishJobOperator getJobOperator(final String name);

    class Duck implements Mutable<DeploymentDef<?,?,?>> {

        private final GlassfishDeployment to;

        public Duck(final GlassfishDeployment to) {
            this.to = to;
        }

        public static List<GlassfishDeclaration> getArtifactLoaders(final GlassfishDeployment that) {
            return that.getArtifactLoader();
        }

        public static void setArtifactLoaders(final GlassfishDeployment that, final List<GlassfishDeclaration> artifactLoaders) {
            that.setArtifactLoader(artifactLoaders);
        }

        public static List<GlassfishJobOperator> getJobOperators(final GlassfishDeployment that) {
            return that.getJobOperator();
        }

        public static void setJobOperators(final GlassfishDeployment that, final List<GlassfishJobOperator> jobOperators) {
            that.setJobOperator(jobOperators);
        }

        public static GlassfishDeclaration getArtifactLoader(final GlassfishDeployment self, final String name) {
            for (final GlassfishDeclaration dep : self.getArtifactLoaders()) {
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

        @Override
        public boolean willAccept(final DeploymentDef<?, ?, ?> that) {
            return to.getName().equals(that.getName());
        }

        @Override
        public void accept(final DeploymentDef<?, ?, ?> from, final Op... ops) throws Exception {
            to.setName(from.getName());
            to.setRef(from.getRef());
            to.setArtifactLoaders(GlassfishTransmute.list(to.getArtifactLoaders(), from.getArtifactLoaders(), new Creator<GlassfishDeclaration>() {
                @Override
                public GlassfishDeclaration create() throws Exception {
                    return to.createChild(GlassfishDeclaration.class);
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
