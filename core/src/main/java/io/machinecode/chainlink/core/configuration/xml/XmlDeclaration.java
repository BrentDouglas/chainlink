package io.machinecode.chainlink.core.configuration.xml;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public class XmlDeclaration extends XmlNamed {

    @XmlAttribute(name = "factory", required = false)
    protected String factory;

    public String getFactory() {
        return factory;
    }

    public void setFactory(final String factory) {
        this.factory = factory;
    }
}
