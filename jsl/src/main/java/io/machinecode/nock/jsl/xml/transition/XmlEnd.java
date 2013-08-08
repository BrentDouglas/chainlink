package io.machinecode.nock.jsl.xml.transition;

import io.machinecode.nock.jsl.api.transition.End;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "End")
public class XmlEnd extends XmlTerminatingTransition<XmlEnd> implements XmlTransition<XmlEnd>, End {

    @Override
    public XmlEnd copy() {
        return copy(new XmlEnd());
    }
}
