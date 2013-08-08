package io.machinecode.nock.jsl.api.execution;

import io.machinecode.nock.jsl.api.Listeners;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.Strategy;
import io.machinecode.nock.jsl.api.task.Task;
import io.machinecode.nock.jsl.api.transition.Transition;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Step<T extends Task, U extends Strategy> extends TransitionExecution {

    String ELEMENT = "step";

    String ZERO = "0";

    String getStartLimit();

    String getAllowStartIfComplete();

    Properties getProperties();

    Listeners getListeners();

    T getTask();

    List<? extends Transition> getTransitions();

    Partition<? extends U> getPartition();
}
