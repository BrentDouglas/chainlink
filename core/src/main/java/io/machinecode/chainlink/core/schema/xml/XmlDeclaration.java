package io.machinecode.chainlink.core.schema.xml;

import io.machinecode.chainlink.core.util.Mutable;
import io.machinecode.chainlink.core.util.Op;
import io.machinecode.chainlink.core.schema.DeclarationSchema;
import io.machinecode.chainlink.core.schema.MutableDeclarationSchema;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public class XmlDeclaration implements MutableDeclarationSchema, Mutable<DeclarationSchema> {

    @XmlID
    @XmlAttribute(name = "name", required = false)
    protected String name;

    @XmlAttribute(name = "ref", required = false)
    protected String ref;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public void setRef(final String ref) {
        this.ref = ref;
    }

    @Override
    public boolean willAccept(final DeclarationSchema that) {
        return name == null || name.equals(that.getName());
    }

    @Override
    public void accept(final DeclarationSchema from, final Op... ops) throws Exception {
        name = from.getName();
        ref = from.getRef();
    }
}
