package io.machinecode.nock.jsl.xml.transition;

import io.machinecode.nock.jsl.xml.TerminatingAttributes;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlFail extends TerminatingAttributes<XmlFail> implements XmlTransition<XmlFail> {

    @Override
    public XmlFail copy() {
        return copy(new XmlFail());
    }
}
