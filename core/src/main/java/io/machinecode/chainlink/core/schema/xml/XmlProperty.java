package io.machinecode.chainlink.core.schema.xml;

import io.machinecode.chainlink.spi.configuration.PropertyModel;
import io.machinecode.chainlink.core.util.Mutable;
import io.machinecode.chainlink.core.util.Op;
import io.machinecode.chainlink.core.schema.MutablePropertySchema;
import io.machinecode.chainlink.core.schema.PropertySchema;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public class XmlProperty implements MutablePropertySchema, Mutable<PropertySchema> {

    public static final String ELEMENT = "property";

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "value", required = true)
    private String value;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(final String value) {
        this.value = value;
    }

    public static void convert(final List<XmlProperty> properties, final PropertyModel target) {
        for (final XmlProperty property : properties) {
            target.setProperty(property.getName(), property.getValue());
        }
    }

    @Override
    public boolean willAccept(final PropertySchema from) {
        return name == null || name.equals(from.getName());
    }

    @Override
    public void accept(final PropertySchema from, final Op... ops) {
        name = from.getName();
        value = from.getValue();
    }
}
