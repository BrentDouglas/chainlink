package io.machinecode.nock.jsl.api.execution;

import io.machinecode.nock.jsl.api.Listeners;
import io.machinecode.nock.jsl.api.Part;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.transition.Transition;

import javax.xml.bind.annotation.XmlAccessorType;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public interface Step<T extends Part, U extends Mapper> extends Execution {

    String ELEMENT = "step";

    String getNext();

    int getStartLimit();

    boolean isAllowStartIfComplete();

    Listeners getListeners();

    Properties getProperties();

    T getPart();

    List<? extends Transition> getTransitions();

    Partition<? extends U> getPartition();
}
