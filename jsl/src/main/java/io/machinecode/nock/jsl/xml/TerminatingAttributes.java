package io.machinecode.nock.jsl.xml;

import io.machinecode.nock.jsl.xml.util.Copyable;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public abstract class TerminatingAttributes<T extends TerminatingAttributes<T>> implements Copyable<T> {

    @XmlAttribute(name = "on", required = true)
    private String on;

    @XmlAttribute(name = "exit-status", required = false)
    private String exitStatus;

    public String getOn() {
        return on;
    }

    public T setOn(final String on) {
        this.on = on;
        return (T)this;
    }

    public String getExitStatus() {
        return exitStatus;
    }

    public T setExitStatus(final String exitStatus) {
        this.exitStatus = exitStatus;
        return (T)this;
    }

    @Override
    public T copy(final T that) {
        that.setOn(this.on);
        that.setExitStatus(this.exitStatus);
        return that;
    }
}
