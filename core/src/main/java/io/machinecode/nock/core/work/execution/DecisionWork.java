package io.machinecode.nock.core.work.execution;

import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.transition.TransitionWork;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.execution.Decision;

import javax.batch.api.Decider;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionWork extends ExecutionWork implements Decision {

    private final ResolvableReference<Decider> decider;
    private final List<TransitionWork> transitions;

    public DecisionWork(final String id, final String ref, final List<TransitionWork> transitions) {
        super(id);
        this.decider = new ResolvableReference<Decider>(ref, Decider.class);
        this.transitions = transitions;
    }

    @Override
    public List<TransitionWork> getTransitions() {
        return this.transitions;
    }

    @Override
    public String getRef() {
        return this.decider.ref();
    }

    @Override
    public Properties getProperties() {
        return null;
    }
}
