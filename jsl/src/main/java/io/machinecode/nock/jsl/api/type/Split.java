package io.machinecode.nock.jsl.api.type;

import javax.xml.bind.annotation.XmlAccessorType;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public interface Split extends Type {

    String getNext();

    List<? extends Flow> getFlows();
}
