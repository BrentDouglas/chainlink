package io.machinecode.nock.jsl.xml.transition;

import io.machinecode.nock.jsl.xml.TerminatingAttributes;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlEnd extends TerminatingAttributes<XmlEnd> implements XmlTransition<XmlEnd> {

    @Override
    public XmlEnd copy() {
        return copy(new XmlEnd());
    }
}
