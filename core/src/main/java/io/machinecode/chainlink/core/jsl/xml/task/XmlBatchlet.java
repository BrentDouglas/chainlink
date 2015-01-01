package io.machinecode.chainlink.core.jsl.xml.task;

import io.machinecode.chainlink.core.jsl.xml.XmlPropertyReference;
import io.machinecode.chainlink.spi.element.task.Batchlet;
import io.machinecode.chainlink.spi.jsl.inherit.task.InheritableTask;

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
