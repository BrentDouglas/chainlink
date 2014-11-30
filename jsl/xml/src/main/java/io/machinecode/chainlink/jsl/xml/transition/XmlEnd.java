package io.machinecode.chainlink.jsl.xml.transition;

import io.machinecode.chainlink.spi.element.transition.End;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "End")
public class XmlEnd extends XmlTerminatingTransition<XmlEnd> implements XmlTransition<XmlEnd>, End {

    @Override
    public XmlEnd copy() {
        return copy(new XmlEnd());
    }
}
