package io.machinecode.nock.jsl.xml;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlListener extends XmlPropertyReference<XmlListener> {

    @Override
    public XmlListener copy() {
        return copy(new XmlListener());
    }
}
