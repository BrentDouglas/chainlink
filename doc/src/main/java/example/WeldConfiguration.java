package example;

import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.inject.cdi.CdiArtifactLoaderFactory;
import io.machinecode.chainlink.inject.cdi.CdiInjectorFactory;
import io.machinecode.chainlink.spi.Constants;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.batch.runtime.BatchRuntime;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class WeldConfiguration {
    public static void main(final String... args) throws Throwable {
        final Weld weld = new Weld();
        final WeldContainer container = weld.initialize();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                weld.shutdown();
            }
        }));

        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final DeploymentModelImpl model = new SubSystemModelImpl(tccl).getDeployment(Constants.DEFAULT);

        final JobOperatorModelImpl op = ManualConfiguration.setDefaults(model, tccl);

        op.getArtifactLoader("weld").setDefaultFactory(new CdiArtifactLoaderFactory(container.getBeanManager()));
        op.getInjector("weld").setDefaultFactory(new CdiInjectorFactory());

        ManualConfiguration.configureAndInstall(model, op, tccl);

        BatchRuntime.getJobOperator().start("a_job", new Properties());
    }
}
