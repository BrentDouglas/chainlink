package io.machinecode.nock.jsl.xml.chunk;

import io.machinecode.nock.jsl.api.chunk.ItemWriter;
import io.machinecode.nock.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class XmlItemWriter extends XmlPropertyReference<XmlItemWriter> implements ItemWriter {

    @Override
    public XmlItemWriter copy() {
        return copy(new XmlItemWriter());
    }
}
