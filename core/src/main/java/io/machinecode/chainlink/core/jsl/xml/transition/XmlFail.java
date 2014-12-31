package io.machinecode.chainlink.core.jsl.xml.transition;

import io.machinecode.chainlink.spi.element.transition.Fail;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Fail")
public class XmlFail extends XmlTerminatingTransition<XmlFail> implements XmlTransition<XmlFail>, Fail {

    @Override
    public XmlFail copy() {
        return copy(new XmlFail());
    }
}
