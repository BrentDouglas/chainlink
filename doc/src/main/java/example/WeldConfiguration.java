package example;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.util.Tccl;
import io.machinecode.chainlink.inject.cdi.CdiArtifactLoaderFactory;
import io.machinecode.chainlink.core.Constants;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.batch.runtime.BatchRuntime;
import java.util.Properties;

import static example.ManualConfiguration.TheEnvironment;
import static example.ManualConfiguration.configureEnvironment;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class WeldConfiguration {
    public static void main(final String... args) throws Throwable {
        final Weld weld = new Weld();
        final WeldContainer container = weld.initialize();

        final ClassLoader tccl = Tccl.get();
        final DeploymentModelImpl model = new SubSystemModelImpl(tccl).getDeployment(Constants.DEFAULT);

        final JobOperatorModelImpl op = ManualConfiguration.setDefaults(model, tccl);

        op.getArtifactLoader("weld").setDefaultFactory(new CdiArtifactLoaderFactory(container.getBeanManager()));

        try (final TheEnvironment environment = configureEnvironment(model, op, tccl)) {
            Chainlink.setEnvironment(environment);
            BatchRuntime.getJobOperator().start("a_job", new Properties());
        } finally {
            Chainlink.setEnvironment(null);
            weld.shutdown();
        }
    }
}
