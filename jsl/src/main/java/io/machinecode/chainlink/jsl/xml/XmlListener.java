package io.machinecode.chainlink.jsl.xml;

import io.machinecode.chainlink.spi.element.Listener;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Listener", propOrder = {
//        "properties"
//})
public class XmlListener extends XmlPropertyReference<XmlListener> implements Listener {

    @Override
    public XmlListener copy() {
        return copy(new XmlListener());
    }
}
