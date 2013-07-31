package io.machinecode.nock.jsl.xml.partition;

import io.machinecode.nock.jsl.api.partition.PartitionReducer;
import io.machinecode.nock.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlPartitionReducer extends XmlPropertyReference<XmlPartitionReducer> implements PartitionReducer {

    @Override
    public XmlPartitionReducer copy() {
        return copy(new XmlPartitionReducer());
    }
}
