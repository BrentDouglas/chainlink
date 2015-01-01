package io.machinecode.chainlink.core.jsl.xml.task;

import io.machinecode.chainlink.core.jsl.xml.XmlPropertyReference;
import io.machinecode.chainlink.spi.element.task.ItemProcessor;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
