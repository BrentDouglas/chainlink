package io.machinecode.chainlink.jsl.xml.partition;

import io.machinecode.chainlink.spi.element.partition.Mapper;
import io.machinecode.chainlink.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
