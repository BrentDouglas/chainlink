package io.machinecode.nock.jsl.xml;

import io.machinecode.nock.jsl.inherit.InheritablePropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import static io.machinecode.nock.spi.element.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
