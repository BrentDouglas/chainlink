package io.machinecode.chainlink.core.schema.xml;

import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlRootElement(namespace = XmlChainlink.NAMESPACE, name = XmlChainlink.ELEMENT)
@XmlAccessorType(NONE)
public class XmlChainlink extends XmlDeployment {

    public static final String ELEMENT = "chainlink";

    public static final String SCHEMA_URL = "http://machinecode.io/xml/ns/chainlink/chainlink_1_0.xsd";
    public static final String NAMESPACE = "http://machinecode.io/xml/ns/chainlink";

    public static void configureDeploymentFromStream(final DeploymentModelImpl model, final ClassLoader loader, final InputStream stream) throws Exception {
        read(stream).configureDeployment(model, loader);
    }

    public static XmlChainlink read(final InputStream stream) throws Exception {
        try {
            final JAXBContext jaxb = JAXBContext.newInstance(XmlChainlink.class);
            final Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            return (XmlChainlink) unmarshaller.unmarshal(stream);
        } finally {
            stream.close();
        }
    }
}
