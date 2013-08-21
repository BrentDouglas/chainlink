package io.machinecode.nock.jsl.xml.transition;

import io.machinecode.nock.spi.element.transition.Stop;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(FIELD)
//@XmlType(name = "Stop")
public class XmlStop extends XmlTerminatingTransition<XmlStop> implements XmlTransition<XmlStop>, Stop {

    @XmlAttribute(name = "restart", required = false)
    private String restart;


    @Override
    public String getRestart() {
        return restart;
    }

    public XmlStop setRestart(final String restart) {
        this.restart = restart;
        return this;
    }

    @Override
    public XmlStop copy() {
        return copy(new XmlStop());
    }

    @Override
    public XmlStop copy(final XmlStop that) {
        that.setRestart(this.restart);
        return super.copy(that);
    }
}
