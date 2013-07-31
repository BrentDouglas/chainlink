package io.machinecode.nock.jsl.impl.type;

import io.machinecode.nock.jsl.api.transition.End;
import io.machinecode.nock.jsl.api.transition.Fail;
import io.machinecode.nock.jsl.api.transition.Next;
import io.machinecode.nock.jsl.api.transition.Stop;
import io.machinecode.nock.jsl.api.transition.Transition;
import io.machinecode.nock.jsl.api.type.Decision;
import io.machinecode.nock.jsl.impl.PropertyReferenceImpl;
import io.machinecode.nock.jsl.impl.transition.EndImpl;
import io.machinecode.nock.jsl.impl.transition.FailImpl;
import io.machinecode.nock.jsl.impl.transition.NextImpl;
import io.machinecode.nock.jsl.impl.transition.StopImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionImpl extends PropertyReferenceImpl implements Decision {

    private final String id;
    private final List<Transition> transitions;

    public DecisionImpl(final Decision that) {
        super(that);
        this.id = that.getId();
        this.transitions = new ArrayList<Transition>(that.getTransitions().size());
        for (final Transition transition : that.getTransitions()) {
            if (transition instanceof End) {
                this.transitions.add(new EndImpl((End)transition));
            } else if (transition instanceof Fail) {
                this.transitions.add(new FailImpl((Fail)transition));
            } else if (transition instanceof Next) {
                this.transitions.add(new NextImpl((Next)transition));
            } else if (transition instanceof Stop) {
                this.transitions.add(new StopImpl((Stop)transition));
            }
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public List<Transition> getTransitions() {
        return this.transitions;
    }
}
