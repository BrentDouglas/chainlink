package io.machinecode.chainlink.core.configuration.xml;

import io.machinecode.chainlink.core.configuration.def.DeclarationDef;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public class XmlDeclaration extends XmlNamed implements DeclarationDef {

    @XmlAttribute(name = "ref", required = false)
    protected String ref;

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public void setRef(final String ref) {
        this.ref = ref;
    }
}
