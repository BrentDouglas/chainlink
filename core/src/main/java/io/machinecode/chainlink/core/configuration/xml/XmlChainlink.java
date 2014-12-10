package io.machinecode.chainlink.core.configuration.xml;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlRootElement(namespace = XmlChainlink.NAMESPACE, name = XmlChainlink.ELEMENT)
@XmlAccessorType(NONE)
public class XmlChainlink extends XmlDeployment {

    public static final String ELEMENT = "chainlink";

    public static final String SCHEMA_URL = "http://io.machinecode/xml/ns/chainlink/chainlink_1_0.xsd";
    public static final String NAMESPACE = "http://io.machinecode/xml/ns/chainlink";
}
