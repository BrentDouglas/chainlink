package io.machinecode.chainlink.jsl.xml.task;

import io.machinecode.chainlink.spi.element.task.ItemReader;
import io.machinecode.chainlink.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
//@XmlType(name = "ItemReader", propOrder = {
//        "properties"
//})
public class XmlItemReader extends XmlPropertyReference<XmlItemReader> implements ItemReader {

    @Override
    public XmlItemReader copy() {
        return copy(new XmlItemReader());
    }
}
