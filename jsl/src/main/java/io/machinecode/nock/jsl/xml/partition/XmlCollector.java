package io.machinecode.nock.jsl.xml.partition;

import io.machinecode.nock.spi.element.partition.Collector;
import io.machinecode.nock.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Collector", propOrder = {
//        "properties"
//})
public class XmlCollector extends XmlPropertyReference<XmlCollector> implements Collector {

    @Override
    public XmlCollector copy() {
        return copy(new XmlCollector());
    }
}
