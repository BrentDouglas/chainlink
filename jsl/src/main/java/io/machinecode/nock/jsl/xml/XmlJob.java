package io.machinecode.nock.jsl.xml;

import io.machinecode.nock.jsl.xml.type.XmlDecision;
import io.machinecode.nock.jsl.xml.type.XmlFlow;
import io.machinecode.nock.jsl.xml.type.XmlSplit;
import io.machinecode.nock.jsl.xml.type.XmlStep;
import io.machinecode.nock.jsl.xml.type.XmlType;
import io.machinecode.nock.jsl.xml.util.Inheritable;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.nock.jsl.xml.XmlJob.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlRootElement(namespace = NAMESPACE, name = "job")
@XmlAccessorType(NONE)
public class XmlJob extends Inheritable<XmlJob> {

    public static final String NAMESPACE = "http://xmlns.jcp.org/xml/ns/javaee";

    @XmlID
    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "version", required = false)
    private String version;

    @XmlAttribute(name = "restartable", required = false)
    private Boolean restartable;

    @XmlElement(name = "properties", namespace = NAMESPACE, required = false, type = XmlProperties.class)
    private XmlProperties properties;

    @XmlElement(name = "listeners", namespace = NAMESPACE, required = false, type = XmlListeners.class)
    private XmlListeners listeners;

    @XmlElements({
            @XmlElement(name = "decision", required = false, namespace = NAMESPACE, type = XmlDecision.class),
            @XmlElement(name = "flow", required = false, namespace = NAMESPACE, type = XmlFlow.class),
            @XmlElement(name = "split", required = false, namespace = NAMESPACE, type = XmlSplit.class),
            @XmlElement(name = "step", required = false, namespace = NAMESPACE, type = XmlStep.class)
    })
    private List<XmlType> types = new ArrayList<XmlType>(0);


    public String getId() {
        return id;
    }

    public XmlJob setId(final String id) {
        this.id = id;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public XmlJob setVersion(final String version) {
        this.version = version;
        return this;
    }

    public Boolean isRestartable() {
        return restartable;
    }

    public XmlJob setRestartable(final Boolean restartable) {
        this.restartable = restartable;
        return this;
    }

    public XmlProperties getProperties() {
        return properties;
    }

    public XmlJob setProperties(final XmlProperties properties) {
        this.properties = properties;
        return this;
    }

    public XmlListeners getListeners() {
        return listeners;
    }

    public XmlJob setListeners(final XmlListeners listeners) {
        this.listeners = listeners;
        return this;
    }

    public List<XmlType> getTypes() {
        return types;
    }

    public XmlJob setTypes(final List<XmlType> types) {
        this.types = types;
        return this;
    }

    @Override
    public XmlJob inherit(final Repository repository) {
        final XmlJob copy = this.copy();
        if (copy.parent != null) {
            final XmlJob that = repository.findParent(XmlJob.class, copy);

            that.getTypes().clear(); // 4.6.1.1

            copy.inheritingElementRule(that); // 4.6.1.2

            // 4.4
            copy.properties = Util.merge(copy.properties, that.properties);
            copy.listeners = Util.merge(copy.listeners, that.listeners);
            // 4.1
            copy.version = Util.attributeRule(copy.version, that.version); // 4.4.1
            copy.restartable = Util.attributeRule(copy.restartable, that.restartable); // 4.4.1
        }
        copy.types = Util.inheritingList(repository, this.types);
        return copy;
    }

    @Override
    public XmlJob copy() {
        return copy(new XmlJob());
    }

    @Override
    public XmlJob copy(final XmlJob that) {
        super.copy(that);
        that.setId(this.id);
        that.setVersion(this.version);
        that.setRestartable(this.restartable);
        that.setProperties(Util.copy(this.properties));
        that.setListeners(Util.copy(this.listeners));
        that.setTypes(Util.copyList(this.types));
        return that;
    }
}
