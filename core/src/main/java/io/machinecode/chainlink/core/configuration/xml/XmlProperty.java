package io.machinecode.chainlink.core.configuration.xml;

import io.machinecode.chainlink.spi.configuration.PropertyModel;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public class XmlProperty {

    public static final String ELEMENT = "property";

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "value", required = true)
    private String value;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public static void convert(final List<XmlProperty> properties, final PropertyModel target) {
        for (final XmlProperty property : properties) {
            target.setProperty(property.getName(), property.getValue());
        }
    }
}
