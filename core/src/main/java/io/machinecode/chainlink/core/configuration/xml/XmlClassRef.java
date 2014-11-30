package io.machinecode.chainlink.core.configuration.xml;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
@XmlAccessorType(NONE)
public class XmlClassRef {

    @XmlAttribute(name = "class", required = true)
    private String clazz;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(final String clazz) {
        this.clazz = clazz;
    }
}
