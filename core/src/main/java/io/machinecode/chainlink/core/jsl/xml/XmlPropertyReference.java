package io.machinecode.chainlink.core.jsl.xml;

import io.machinecode.chainlink.spi.jsl.inherit.InheritablePropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import static io.machinecode.chainlink.spi.jsl.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public abstract class XmlPropertyReference<T extends XmlPropertyReference<T>> implements InheritablePropertyReference<T, XmlProperties> {

    @XmlAttribute(name = "ref", required = true)
    private String ref;

    @XmlElement(name = "properties", namespace = NAMESPACE, required = false)
    private XmlProperties properties;


    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public T setRef(final String ref) {
        this.ref = ref;
        return (T)this;
    }

    @Override
    public XmlProperties getProperties() {
        return properties;
    }

    @Override
    public T setProperties(final XmlProperties properties) {
        this.properties = properties;
        return (T)this;
    }

    @Override
    public T copy(final T that) {
        return PropertyReferenceTool.copy((T)this, that);
    }

    @Override
    public T merge(final T that) {
        return PropertyReferenceTool.merge((T)this, that);
    }
}
