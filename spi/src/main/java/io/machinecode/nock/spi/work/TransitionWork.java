package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.element.transition.Transition;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface TransitionWork extends Transition, Serializable {

    String element();

    BatchStatus getBatchStatus();

    String getExitStatus();

    String getNext();

    String getRestartId();

    boolean isTerminating();
}
