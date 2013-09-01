package io.machinecode.nock.jsl.xml.partition;

import io.machinecode.nock.spi.element.partition.Analyser;
import io.machinecode.nock.jsl.xml.XmlPropertyReference;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
//@XmlType(name = "Analyser", propOrder = {
//        "properties"
//})
public class XmlAnalyser extends XmlPropertyReference<XmlAnalyser> implements Analyser {

    @Override
    public XmlAnalyser copy() {
        return copy(new XmlAnalyser());
    }
}
