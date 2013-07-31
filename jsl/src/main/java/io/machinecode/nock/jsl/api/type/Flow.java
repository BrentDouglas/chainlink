package io.machinecode.nock.jsl.api.type;

import io.machinecode.nock.jsl.api.transition.Transition;

import javax.xml.bind.annotation.XmlAccessorType;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public interface Flow extends Type {

    String getNext();

    List<? extends Type> getTypes();

    List<? extends Transition> getTransitions();
}
