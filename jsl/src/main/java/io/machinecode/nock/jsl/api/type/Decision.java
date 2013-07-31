package io.machinecode.nock.jsl.api.type;

import io.machinecode.nock.jsl.api.PropertyReference;
import io.machinecode.nock.jsl.api.transition.Transition;

import javax.xml.bind.annotation.XmlAccessorType;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public interface Decision extends Type, PropertyReference {

    List<? extends Transition> getTransitions();
}
