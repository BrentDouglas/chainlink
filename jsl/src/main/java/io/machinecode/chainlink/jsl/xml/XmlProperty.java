package io.machinecode.chainlink.jsl.xml;

import io.machinecode.chainlink.jsl.inherit.InheritableProperty;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Property")
public class XmlProperty implements InheritableProperty<XmlProperty> {

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "value", required = true)
    private String value;


    @Override
    public String getName() {
        return name;
    }

    public XmlProperty setName(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getValue() {
        return value;
    }

    public XmlProperty setValue(final String value) {
        this.value = value;
        return this;
    }

    @Override
    public XmlProperty copy() {
        return copy(new XmlProperty());
    }

    @Override
    public XmlProperty copy(final XmlProperty that) {
        return PropertyTool.copy(this, that);
    }
}
