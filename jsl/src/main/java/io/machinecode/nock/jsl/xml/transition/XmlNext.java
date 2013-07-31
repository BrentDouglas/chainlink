package io.machinecode.nock.jsl.xml.transition;

import io.machinecode.nock.jsl.api.transition.Next;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(FIELD)
public class XmlNext implements XmlTransition<XmlNext>, Next {

    @XmlAttribute(name = "on", required = true)
    private String on;

    @XmlAttribute(name = "to", required = true)
    private String to;


    @Override
    public String getOn() {
        return on;
    }

    public XmlNext setOn(final String on) {
        this.on = on;
        return this;
    }

    @Override
    public String getTo() {
        return to;
    }

    public XmlNext setTo(final String to) {
        this.to = to;
        return this;
    }

    @Override
    public XmlNext copy() {
        return copy(new XmlNext());
    }

    @Override
    public XmlNext copy(final XmlNext that) {
        that.setOn(this.on);
        that.setTo(this.to);
        return that;
    }
}
