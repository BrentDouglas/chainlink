package io.machinecode.chainlink.core.jsl.xml.partition;

import io.machinecode.chainlink.core.jsl.xml.XmlPropertyReference;
import io.machinecode.chainlink.spi.jsl.partition.Mapper;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
//@XmlType(name = "PartitionMapper", propOrder = {
//        "properties"
//})
public class XmlMapper extends XmlPropertyReference<XmlMapper> implements XmlStrategy<XmlMapper>, Mapper {

    @Override
    public XmlMapper copy() {
        return copy(new XmlMapper());
    }
}
