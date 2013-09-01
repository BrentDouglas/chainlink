package io.machinecode.nock.jsl.xml.task;

import io.machinecode.nock.spi.element.task.ItemProcessor;
import io.machinecode.nock.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
