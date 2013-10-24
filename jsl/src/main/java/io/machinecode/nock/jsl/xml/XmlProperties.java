package io.machinecode.nock.jsl.xml;

import io.machinecode.nock.jsl.inherit.InheritableProperties;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.nock.spi.element.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Properties", propOrder = {
//        "properties"
//})
public class XmlProperties extends XmlMergeableList<XmlProperties> implements InheritableProperties<XmlProperties, XmlProperty> {

    @XmlAttribute(name = "partition", required = false)
    private String partition;

    @XmlElement(name = "property", namespace = NAMESPACE, required = false)
    private List<XmlProperty> properties = new ArrayList<XmlProperty>(0);

    @Override
    public String getPartition() {
        return partition;
    }

    public XmlProperties setPartition(final String partition) {
        this.partition = partition;
        return this;
    }

    @Override
    public List<XmlProperty> getProperties() {
        return properties;
    }

    public XmlProperties setProperties(final List<XmlProperty> properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public XmlProperties copy() {
        return copy(new XmlProperties());
    }

    @Override
    public XmlProperties copy(final XmlProperties that) {
        return PropertiesTool.copy(this, that);
        //that.setProperties(Util.copyList(this.properties));
        //return that;
    }

    @Override
    public XmlProperties merge(final XmlProperties that) {
        return PropertiesTool.merge(this, that);
        //if (this.merge) {
        //    this.properties = Util.listRule(this.properties, that.properties);
        //}
        //return this;
    }
}
