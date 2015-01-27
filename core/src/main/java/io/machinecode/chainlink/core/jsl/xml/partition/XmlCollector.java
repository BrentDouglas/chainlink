package io.machinecode.chainlink.core.jsl.xml.partition;

import io.machinecode.chainlink.core.jsl.xml.XmlPropertyReference;
import io.machinecode.chainlink.spi.jsl.partition.Collector;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
