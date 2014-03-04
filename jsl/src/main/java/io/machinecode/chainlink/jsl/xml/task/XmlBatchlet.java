package io.machinecode.chainlink.jsl.xml.task;

import io.machinecode.chainlink.jsl.inherit.task.InheritableTask;
import io.machinecode.chainlink.jsl.xml.XmlPropertyReference;
import io.machinecode.chainlink.spi.element.task.Batchlet;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Batchlet", propOrder = {
//        "properties"
//})
public class XmlBatchlet extends XmlPropertyReference<XmlBatchlet> implements InheritableTask<XmlBatchlet>, XmlTask<XmlBatchlet>, Batchlet {

    @Override
    public XmlBatchlet copy() {
        return copy(new XmlBatchlet());
    }
}
