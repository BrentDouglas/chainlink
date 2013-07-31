package io.machinecode.nock.jsl.api.type;

import io.machinecode.nock.jsl.api.Listeners;
import io.machinecode.nock.jsl.api.Part;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.transition.Transition;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Step<T extends Part, U extends Mapper> extends Type {

    String getNext();

    String getStartLimit();

    String getAllowStartIfComplete();

    Listeners getListeners();

    Properties getProperties();

    T getPart();

    List<Transition> getTransitions();

    Partition<U> getPartition();
}
