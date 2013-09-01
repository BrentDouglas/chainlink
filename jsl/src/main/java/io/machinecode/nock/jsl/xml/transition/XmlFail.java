package io.machinecode.nock.jsl.xml.transition;

import io.machinecode.nock.spi.element.transition.Fail;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Fail")
public class XmlFail extends XmlTerminatingTransition<XmlFail> implements XmlTransition<XmlFail>, Fail {

    @Override
    public XmlFail copy() {
        return copy(new XmlFail());
    }
}
