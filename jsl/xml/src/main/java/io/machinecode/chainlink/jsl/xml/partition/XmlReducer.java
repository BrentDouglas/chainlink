package io.machinecode.chainlink.jsl.xml.partition;

import io.machinecode.chainlink.spi.element.partition.Reducer;
import io.machinecode.chainlink.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
//@XmlType(name = "PartitionReducer", propOrder = {
//        "properties"
//})
public class XmlReducer extends XmlPropertyReference<XmlReducer> implements Reducer {

    @Override
    public XmlReducer copy() {
        return copy(new XmlReducer());
    }
}
