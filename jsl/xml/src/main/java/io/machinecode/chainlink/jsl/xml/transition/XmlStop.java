package io.machinecode.chainlink.jsl.xml.transition;

import io.machinecode.chainlink.jsl.core.inherit.transition.InheritableStop;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(FIELD)
//@XmlType(name = "Stop")
public class XmlStop extends XmlTerminatingTransition<XmlStop> implements XmlTransition<XmlStop>, InheritableStop<XmlStop> {

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
        return StopTool.copy(this, that);
        //that.setRestart(this.restart);
        //return super.copy(that);
    }
}
