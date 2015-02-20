package io.machinecode.chainlink.rt.glassfish.configuration;

import io.machinecode.chainlink.core.configuration.xml.XmlDeclaration;
import io.machinecode.chainlink.core.configuration.xml.XmlDeployment;
import io.machinecode.chainlink.core.configuration.xml.XmlJobOperator;
import io.machinecode.chainlink.core.configuration.xml.XmlProperty;
import io.machinecode.chainlink.core.configuration.xml.subsystem.XmlChainlinkSubSystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public final class GlassfishXml {

    public static XmlChainlinkSubSystem xmlSubSystem(final GlassfishSubSystem subSystem) throws Exception {
        final XmlChainlinkSubSystem model = new XmlChainlinkSubSystem();
        for (final GlassfishDeclaration dec : subSystem.getConfigurationLoaders()) {
            model.getConfigurationLoaders().add(xmlDec(dec));
        }
        for (final GlassfishJobOperator dec : subSystem.getJobOperators()) {
            model.getJobOperators().add(xmlJobOperator(dec));
        }
        for (final GlassfishDeployment dec : subSystem.getDeployments()) {
            model.getDeployments().add(xmlDeployment(dec));
        }
        model.setRef(subSystem.getRef());
        return model;
    }

    public static XmlDeployment xmlDeployment(final GlassfishDeployment deployment) throws Exception {
        final XmlDeployment model = new XmlDeployment();
        model.setName(deployment.getName());
        for (final GlassfishDeclaration dec : deployment.getConfigurationLoaders()) {
            model.getConfigurationLoaders().add(xmlDec(dec));
        }
        for (final GlassfishJobOperator dec : deployment.getJobOperators()) {
            model.getJobOperators().add(xmlJobOperator(dec));
        }
        model.setRef(deployment.getRef());
        return model;
    }

    public static XmlJobOperator xmlJobOperator(final GlassfishJobOperator op) throws Exception {
        final XmlJobOperator model = new XmlJobOperator();
        model.setName(op.getName());
        model.setProperties(xmlProperties(op.getProperties()));
        model.setExecutor(xmlDec(op.getExecutor()));
        model.setTransport(xmlDec(op.getTransport()));
        model.setMarshalling(xmlDec(op.getMarshalling()));
        model.setRegistry(xmlDec(op.getRegistry()));
        model.setMBeanServer(xmlDec(op.getMBeanServer()));
        model.setRepository(xmlDec(op.getRepository()));
        model.setClassLoader(xmlDec(op.getClassLoader()));
        model.setTransactionManager(xmlDec(op.getTransactionManager()));
        for (final GlassfishDeclaration resource : op.getJobLoaders()) {
            model.getJobLoaders().add(xmlDec(resource));
        }
        for (final GlassfishDeclaration resource : op.getArtifactLoaders()) {
            model.getArtifactLoaders().add(xmlDec(resource));
        }
        for (final GlassfishDeclaration resource : op.getSecurities()) {
            model.getSecurities().add(xmlDec(resource));
        }
        model.setRef(op.getRef());
        return model;
    }

    private static List<XmlProperty> xmlProperties(final List<GlassfishProperty> properties) {
        final List<XmlProperty> model = new ArrayList<>(properties.size());
        for (final GlassfishProperty dec : properties) {
            final XmlProperty xml = new XmlProperty();
            xml.setName(dec.getName());
            xml.setValue(dec.getValue());
            model.add(xml);
        }
        return model;
    }

    private static XmlDeclaration xmlDec(final GlassfishDeclaration dec) {
        if (dec == null) {
            return null;
        }
        final XmlDeclaration model = new XmlDeclaration();
        model.setName(dec.getName());
        model.setRef(dec.getRef());
        return model;
    }

    public static ByteArrayInputStream writeDeployment(final XmlDeployment that) throws Exception {
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            that.write(stream);
            return new ByteArrayInputStream(stream.toByteArray());
        }
    }

    public static ByteArrayInputStream writeSubSystem(final XmlChainlinkSubSystem that) throws Exception {
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            that.write(stream);
            return new ByteArrayInputStream(stream.toByteArray());
        }
    }

    public static ByteArrayInputStream writeJobOperator(final XmlJobOperator that) throws Exception {
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            that.write(stream);
            return new ByteArrayInputStream(stream.toByteArray());
        }
    }
}
