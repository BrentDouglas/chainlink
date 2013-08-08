package io.machinecode.nock.jsl.xml;

import io.machinecode.nock.jsl.api.task.Batchlet;
import io.machinecode.nock.jsl.xml.task.XmlTask;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Batchlet", propOrder = {
//        "properties"
//})
public class XmlBatchlet extends XmlPropertyReference<XmlBatchlet> implements XmlTask<XmlBatchlet>, Batchlet {

    @Override
    public XmlBatchlet copy() {
        return copy(new XmlBatchlet());
    }
}
