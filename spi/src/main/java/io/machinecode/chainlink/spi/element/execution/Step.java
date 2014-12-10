package io.machinecode.chainlink.spi.element.execution;

import io.machinecode.chainlink.spi.element.Listeners;
import io.machinecode.chainlink.spi.element.PropertiesElement;
import io.machinecode.chainlink.spi.element.partition.Partition;
import io.machinecode.chainlink.spi.element.partition.Strategy;
import io.machinecode.chainlink.spi.element.task.Task;
import io.machinecode.chainlink.spi.element.transition.Transition;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
