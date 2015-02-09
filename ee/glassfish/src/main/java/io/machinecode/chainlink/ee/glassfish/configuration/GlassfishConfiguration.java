package io.machinecode.chainlink.ee.glassfish.configuration;

import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.ScopeModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.spi.configuration.Declaration;
import io.machinecode.chainlink.spi.configuration.DeploymentConfiguration;
import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.PropertyModel;
import io.machinecode.chainlink.spi.configuration.SubSystemConfiguration;
import io.machinecode.chainlink.spi.exception.ConfigurationException;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public final class GlassfishConfiguration {

    public static void configureSubSystem(final SubSystemModelImpl model, final GlassfishSubSystem subSystem, final ClassLoader classLoader) throws Exception {
        for (final GlassfishDeclaration dec : subSystem.getArtifactLoaders()) {
            model.getArtifactLoader(dec.getName()).setRef(dec.getRef());
        }
        for (final GlassfishJobOperator dec : subSystem.getJobOperators()) {
            configureScope(model, dec, classLoader);
        }
        for (final GlassfishDeployment dec : subSystem.getDeployments()) {
            configureDeployment(model.getDeployment(dec.getName()), dec, classLoader);
        }
        if (subSystem.getRef() != null) {
            final SubSystemConfiguration configuration;
            try {
                configuration = model.getConfigurationArtifactLoader().load(subSystem.getRef(), SubSystemConfiguration.class, classLoader);
            } catch (final Exception e) {
                throw new ConfigurationException("attribute 'ref' must be an injectable " + SubSystemConfiguration.class.getName(), e); //TODO Message
            }
            configuration.configureSubSystem(model);
        }
    }

    private static void configureDeployment(final DeploymentModelImpl model, final GlassfishDeployment deployment, final ClassLoader classLoader) throws Exception {
        for (final GlassfishDeclaration dec : deployment.getArtifactLoaders()) {
            model.getArtifactLoader(dec.getName()).setRef(dec.getRef());
        }
        for (final GlassfishJobOperator dec : deployment.getJobOperators()) {
            configureScope(model, dec, classLoader);
        }
        if (deployment.getRef() != null) {
            final DeploymentConfiguration configuration;
            try {
                configuration = model.getConfigurationArtifactLoader().load(deployment.getRef(), DeploymentConfiguration.class, classLoader);
            } catch (final Exception e) {
                throw new ConfigurationException("attribute 'ref' must be an injectable " + DeploymentConfiguration.class.getName(), e); //TODO Message
            }
            configuration.configureDeployment(model);
        }
    }

    private static void configureScope(final ScopeModelImpl scope, final GlassfishJobOperator op, final ClassLoader classLoader) throws Exception {
        final JobOperatorModel model = scope.getJobOperator(op.getName());

        properties(op.getProperties(), model.getProperties());

        set(model.getExecutor()
                .setName(name(op.getExecutor(), JobOperatorModelImpl.EXECUTOR)), ref(op.getExecutor()));
        set(model.getTransport()
                .setName(name(op.getTransport(), JobOperatorModelImpl.TRANSPORT)), ref(op.getTransport()));
        set(model.getMarshalling()
                .setName(name(op.getMarshalling(), JobOperatorModelImpl.MARSHALLING)), ref(op.getMarshalling()));
        set(model.getRegistry()
                .setName(name(op.getRegistry(), JobOperatorModelImpl.REGISTRY)), ref(op.getRegistry()));
        if (op.getMBeanServer() != null) {
            model.getMBeanServer() //This is nullable, calling getMBeanServer() will add it to the model
                    .setName(name(op.getMBeanServer(), JobOperatorModelImpl.MBEAN_SERVER))
                    .setRef(ref(op.getMBeanServer()));
        }
        set(model.getRepository()
                .setName(name(op.getRepository(), JobOperatorModelImpl.EXECUTION_REPOSITORY)), ref(op.getRepository()));
        set(model.getClassLoader()
                .setName(name(op.getClassLoader(), JobOperatorModelImpl.CLASS_LOADER)), ref(op.getClassLoader()));
        set(model.getTransactionManager()
                .setName(name(op.getTransactionManager(), JobOperatorModelImpl.TRANSACTION_MANAGER)), ref(op.getTransactionManager()));
        for (final GlassfishDeclaration resource : op.getJobLoaders()) {
            model.getJobLoader(resource.getName())
                    .setRef(ref(resource));
        }
        for (final GlassfishDeclaration resource : op.getArtifactLoaders()) {
            model.getArtifactLoader(resource.getName())
                    .setRef(ref(resource));
        }
        for (final GlassfishDeclaration resource : op.getInjectors()) {
            model.getInjector(resource.getName())
                    .setRef(ref(resource));
        }
        for (final GlassfishDeclaration resource : op.getSecurities()) {
            model.getSecurity(resource.getName())
                    .setRef(ref(resource));
        }
        if (op.getRef() != null) {
            final JobOperatorConfiguration configuration;
            try {
                configuration = scope.getConfigurationArtifactLoader().load(op.getRef(), JobOperatorConfiguration.class, classLoader);
            } catch (final ArtifactOfWrongTypeException e) {
                throw new ConfigurationException("attribute 'ref' must be the fqcn of a class extending " + JobOperatorConfiguration.class.getName(), e); //TODO Message
            }
            configuration.configureJobOperator(model);
        }
    }

    private static void properties(final List<GlassfishProperty> properties, final PropertyModel target) {
        for (final GlassfishProperty property : properties) {
            target.setProperty(property.getName(), property.getValue());
        }
    }

    private static String name(final GlassfishDeclaration dec, final String def) {
        return dec == null ? def : dec.getName();
    }

    private static String ref(final GlassfishDeclaration dec) {
        return dec == null ? null : dec.getRef();
    }

    private static void set(final Declaration<?> dec, final String ref) {
        if (ref == null || ref.trim().isEmpty()) {
            return;
        }
        dec.setRef(ref);
    }
}
