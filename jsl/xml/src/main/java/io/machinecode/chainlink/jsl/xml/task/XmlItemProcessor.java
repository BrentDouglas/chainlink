package io.machinecode.chainlink.jsl.xml.task;

import io.machinecode.chainlink.spi.element.task.ItemProcessor;
import io.machinecode.chainlink.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "ItemProcessor", propOrder = {
//        "properties"
//})
public class XmlItemProcessor extends XmlPropertyReference<XmlItemProcessor> implements ItemProcessor {

    @Override
    public XmlItemProcessor copy() {
        return copy(new XmlItemProcessor());
    }
}
