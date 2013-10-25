package io.machinecode.nock.spi.element.execution;

import io.machinecode.nock.spi.PropertiesElement;
import io.machinecode.nock.spi.element.Listeners;
import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.element.partition.Strategy;
import io.machinecode.nock.spi.element.task.Task;
import io.machinecode.nock.spi.element.transition.Transition;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Step<T extends Task, U extends Strategy> extends TransitionExecution, PropertiesElement {

    String ELEMENT = "step";

    String ZERO = "0";

    String getStartLimit();

    String getAllowStartIfComplete();

    Listeners getListeners();

    T getTask();

    List<? extends Transition> getTransitions();

    Partition<? extends U> getPartition();
}
