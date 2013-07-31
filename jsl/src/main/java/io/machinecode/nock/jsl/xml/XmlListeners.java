package io.machinecode.nock.jsl.xml;

import io.machinecode.nock.jsl.xml.util.MergeableList;
import io.machinecode.nock.jsl.xml.util.Util;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

import static io.machinecode.nock.jsl.xml.XmlJob.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlListeners extends MergeableList<XmlListeners> {

    @XmlElement(name = "listener", namespace = NAMESPACE, required = false)
    private List<XmlListener> listeners = new ArrayList<XmlListener>(0);

    public List<XmlListener> getListeners() {
        return listeners;
    }

    public XmlListeners setProperties(final List<XmlListener> properties) {
        this.listeners = properties;
        return this;
    }

    @Override
    public XmlListeners copy() {
        return copy(new XmlListeners());
    }

    @Override
    public XmlListeners copy(final XmlListeners that) {
        that.setProperties(Util.copyList(this.listeners));
        return that;
    }

    @Override
    public XmlListeners merge(final XmlListeners that) {
        if (this.merge) {
            this.listeners = Util.listRule(this.listeners, that.listeners);
        }
        return this;
    }
}
