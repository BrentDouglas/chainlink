package io.machinecode.nock.jsl.api.execution;

import io.machinecode.nock.jsl.api.transition.Transition;

import javax.xml.bind.annotation.XmlAccessorType;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public interface Flow extends Execution {

    String getNext();

    List<? extends Execution> getExecutions();

    List<? extends Transition> getTransitions();
}
