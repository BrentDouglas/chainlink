package io.machinecode.nock.jsl.api;

import javax.xml.bind.annotation.XmlAccessorType;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public interface Properties {

    String ELEMENT = "properties";

    List<? extends Property> getProperties();

    String getPartition();
}
