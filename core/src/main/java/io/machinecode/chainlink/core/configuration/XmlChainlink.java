package io.machinecode.chainlink.core.configuration;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlRootElement(namespace = XmlChainlink.NAMESPACE, name = XmlChainlink.ELEMENT)
@XmlAccessorType(NONE)
public class XmlChainlink {

    public static final String ELEMENT = "chainlink";

    public static final String SCHEMA_URL = "http://xmlns.io.machinecode/xml/ns/chainlink/chainlink_1_0.xsd";
    public static final String NAMESPACE = "http://xmlns.io.machinecode/xml/ns/chainlink";

    @XmlElement(name = "environment", namespace = NAMESPACE, required = true)
    private XmlClassRef environment;

    @XmlElement(name = "configuration", namespace = NAMESPACE, required = false)
    private List<XmlConfiguration> configurations = new ArrayList<XmlConfiguration>(0);

    public XmlClassRef getEnvironment() {
        return environment;
    }

    public void setEnvironment(final XmlClassRef environment) {
        this.environment = environment;
    }

    public List<XmlConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(final List<XmlConfiguration> configurations) {
        this.configurations = configurations;
    }
}
