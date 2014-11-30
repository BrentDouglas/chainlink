package io.machinecode.chainlink.jsl.xml.task;

import io.machinecode.chainlink.spi.element.task.ItemWriter;
import io.machinecode.chainlink.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "ItemWriter", propOrder = {
//        "properties"
//})
public class XmlItemWriter extends XmlPropertyReference<XmlItemWriter> implements ItemWriter {

    @Override
    public XmlItemWriter copy() {
        return copy(new XmlItemWriter());
    }
}
