package io.machinecode.chainlink.jsl.xml.task;

import io.machinecode.chainlink.jsl.core.inherit.task.InheritableTask;
import io.machinecode.chainlink.jsl.xml.XmlPropertyReference;
import io.machinecode.chainlink.spi.element.task.Batchlet;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
