package io.machinecode.nock.jsl.xml;

import io.machinecode.nock.jsl.api.Batchlet;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlBatchlet extends XmlPropertyReference<XmlBatchlet> implements XmlPart<XmlBatchlet>, Batchlet {

    @Override
    public XmlBatchlet copy() {
        return copy(new XmlBatchlet());
    }
}
