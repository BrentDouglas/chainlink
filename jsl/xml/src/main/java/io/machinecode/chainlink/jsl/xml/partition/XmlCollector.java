package io.machinecode.chainlink.jsl.xml.partition;

import io.machinecode.chainlink.spi.element.partition.Collector;
import io.machinecode.chainlink.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
