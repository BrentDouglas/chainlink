package io.machinecode.chainlink.spi.work;

import io.machinecode.chainlink.spi.element.transition.Transition;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface TransitionWork extends Transition, Serializable {

    String element();

    BatchStatus getBatchStatus();

    String getExitStatus();

    String getNext();

    String getRestartId();

    boolean isTerminating();
}
