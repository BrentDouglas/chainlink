package io.machinecode.nock.jsl.xml;

import io.machinecode.nock.jsl.inherit.InheritableListeners;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.nock.spi.element.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Listeners", propOrder = {
//        "listeners"
//})
public class XmlListeners extends XmlMergeableList<XmlListeners> implements InheritableListeners<XmlListeners, XmlListener> {

    @XmlElement(name = "listener", namespace = NAMESPACE, required = false)
    private List<XmlListener> listeners = new ArrayList<XmlListener>(0);

    @Override
    public List<XmlListener> getListeners() {
        return listeners;
    }

    public XmlListeners setListeners(final List<XmlListener> listeners) {
        this.listeners = listeners;
        return this;
    }

    @Override
    public XmlListeners copy() {
        return copy(new XmlListeners());
    }

    @Override
    public XmlListeners copy(final XmlListeners that) {
        return ListenersTool.copy(this, that);
        //that.setListeners(Util.copyList(this.listeners));
        //return that;
    }

    @Override
    public XmlListeners merge(final XmlListeners that) {
        return ListenersTool.merge(this, that);
        //if (this.merge) {
        //    this.listeners = Util.listRule(this.listeners, that.listeners);
        //}
        //return this;
    }
}
