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

    @XmlAttribute(name = "ref", required = false)
    protected String ref;

    public String getRef() {
        return ref;
    }

    public void setRef(final String ref) {
        this.ref = ref;
    }
}
