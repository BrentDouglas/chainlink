package io.machinecode.chainlink.jsl.xml;

import io.machinecode.chainlink.jsl.core.inherit.InheritableListeners;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.chainlink.spi.element.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
    }

    @Override
    public XmlListeners merge(final XmlListeners that) {
        return ListenersTool.merge(this, that);
    }
}
