package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.schema.DeclarationSchema;
import io.machinecode.chainlink.core.schema.DeploymentSchema;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.PropertySchema;
import io.machinecode.chainlink.core.schema.SubSystemSchema;
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
 * <p>Utility to configure a model from a schema.</p>
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public final class Model {

    public static void configureSubSystem(final SubSystemModelImpl model, final SubSystemSchema<?, ?, ?, ?> subSystem, final ClassLoader classLoader) throws Exception {
        for (final DeclarationSchema dec : subSystem.getConfigurationLoaders()) {
            model.getConfigurationLoader(dec.getName()).setRef(dec.getRef());
        }
        for (final JobOperatorSchema<?, ?> dec : subSystem.getJobOperators()) {
            configureJobOperator(model, dec, classLoader);
        }
        for (final DeploymentSchema<?, ?, ?> dec : subSystem.getDeployments()) {
            configureDeployment(model.getDeployment(dec.getName()), dec, classLoader);
        }
        if (subSystem.getRef() != null) {
            final SubSystemConfiguration configuration;
            try {
                configuration = model.getConfigurationLoader().load(subSystem.getRef(), SubSystemConfiguration.class, classLoader);
            } catch (final Exception e) {
                throw new ConfigurationException("attribute 'ref' must be an injectable " + SubSystemConfiguration.class.getName(), e); //TODO Message
            }
            configuration.configureSubSystem(model);
        }
    }

    public static void configureDeployment(final DeploymentModelImpl model, final DeploymentSchema<?, ?, ?> deployment, final ClassLoader classLoader) throws Exception {
        for (final DeclarationSchema dec : deployment.getConfigurationLoaders()) {
            model.getConfigurationLoader(dec.getName()).setRef(dec.getRef());
        }
        for (final JobOperatorSchema<?,?> dec : deployment.getJobOperators()) {
            configureJobOperator(model, dec, classLoader);
        }
        if (deployment.getRef() != null) {
            final DeploymentConfiguration configuration;
            try {
                configuration = model.getConfigurationLoader().load(deployment.getRef(), DeploymentConfiguration.class, classLoader);
            } catch (final Exception e) {
                throw new ConfigurationException("attribute 'ref' must be an injectable " + DeploymentConfiguration.class.getName(), e); //TODO Message
            }
            configuration.configureDeployment(model);
        }
    }

    public static void configureJobOperator(final ScopeModelImpl scope, final JobOperatorSchema<?, ?> op, final ClassLoader classLoader) throws Exception {
        final JobOperatorModel model = scope.getJobOperator(op.getName());

        properties(op.getProperties(), model.getProperties());

        set(model.getExecutor(), name(op.getExecutor(), JobOperatorModelImpl.EXECUTOR), ref(op.getExecutor()));
        set(model.getTransport(), name(op.getTransport(), JobOperatorModelImpl.TRANSPORT), ref(op.getTransport()));
        set(model.getMarshalling(), name(op.getMarshalling(), JobOperatorModelImpl.MARSHALLING), ref(op.getMarshalling()));
        set(model.getRegistry(), name(op.getRegistry(), JobOperatorModelImpl.REGISTRY), ref(op.getRegistry()));
        if (op.getMBeanServer() != null) {
            model.getMBeanServer() //This is nullable, calling getMBeanServer() will add it to the model
                    .setName(name(op.getMBeanServer(), JobOperatorModelImpl.MBEAN_SERVER))
                    .setRef(ref(op.getMBeanServer()));
        }
        set(model.getRepository(), name(op.getRepository(), JobOperatorModelImpl.EXECUTION_REPOSITORY), ref(op.getRepository()));
        set(model.getClassLoader(), name(op.getClassLoader(), JobOperatorModelImpl.CLASS_LOADER), ref(op.getClassLoader()));
        set(model.getTransactionManager(), name(op.getTransactionManager(), JobOperatorModelImpl.TRANSACTION_MANAGER), ref(op.getTransactionManager()));
        for (final DeclarationSchema resource : op.getJobLoaders()) {
            model.getJobLoader(resource.getName())
                    .setRef(ref(resource));
        }
        for (final DeclarationSchema resource : op.getArtifactLoaders()) {
            model.getArtifactLoader(resource.getName())
                    .setRef(ref(resource));
        }
        for (final DeclarationSchema resource : op.getSecurities()) {
            model.getSecurity(resource.getName())
                    .setRef(ref(resource));
        }
        if (op.getRef() != null) {
            final JobOperatorConfiguration configuration;
            try {
                configuration = scope.getConfigurationLoader().load(op.getRef(), JobOperatorConfiguration.class, classLoader);
            } catch (final ArtifactOfWrongTypeException e) {
                throw new ConfigurationException("attribute 'ref' must be the fqcn of a class extending " + JobOperatorConfiguration.class.getName(), e); //TODO Message
            }
            configuration.configureJobOperator(model);
        }
    }

    private static void properties(final List<? extends PropertySchema> properties, final PropertyModel target) {
        for (final PropertySchema property : properties) {
            target.setProperty(property.getName(), property.getValue());
        }
    }

    private static String name(final DeclarationSchema dec, final String def) {
        return dec == null ? def : dec.getName();
    }

    private static String ref(final DeclarationSchema dec) {
        return dec == null ? null : dec.getRef();
    }

    private static void set(final Declaration<?> dec, final String name, final String ref) {
        if (name == null || name.trim().isEmpty() || ref == null || ref.trim().isEmpty()) {
            return;
        }
        dec.setName(name);
        dec.setRef(ref);
    }
}
