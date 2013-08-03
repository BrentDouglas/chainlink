package io.machinecode.nock.jsl.xml.partition;

import io.machinecode.nock.jsl.api.partition.Reducer;
import io.machinecode.nock.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlReducer extends XmlPropertyReference<XmlReducer> implements Reducer {

    @Override
    public XmlReducer copy() {
        return copy(new XmlReducer());
    }
}
