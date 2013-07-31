package io.machinecode.nock.jsl.impl.type;

import io.machinecode.nock.jsl.api.Listeners;
import io.machinecode.nock.jsl.api.Part;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.transition.End;
import io.machinecode.nock.jsl.api.transition.Fail;
import io.machinecode.nock.jsl.api.transition.Next;
import io.machinecode.nock.jsl.api.transition.Stop;
import io.machinecode.nock.jsl.api.transition.Transition;
import io.machinecode.nock.jsl.api.type.Step;
import io.machinecode.nock.jsl.impl.ListenersImpl;
import io.machinecode.nock.jsl.impl.PropertiesImpl;
import io.machinecode.nock.jsl.impl.transition.EndImpl;
import io.machinecode.nock.jsl.impl.transition.FailImpl;
import io.machinecode.nock.jsl.impl.transition.NextImpl;
import io.machinecode.nock.jsl.impl.transition.StopImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class StepImpl<T extends Part, U extends Mapper> extends TypeImpl implements Step<T, U> {

    private final String next;
    private final String startLimit;
    private final String allowStartIfComplete;
    private final Listeners listeners;
    private final Properties properties;
    private final List<Transition> transitions;

    public StepImpl(final Step<T, U> that) {
        super(that);
        this.next = that.getNext();
        this.startLimit = that.getStartLimit();
        this.allowStartIfComplete = that.getAllowStartIfComplete();
        this.listeners = new ListenersImpl(that.getListeners());
        this.properties = new PropertiesImpl(that.getProperties());
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
    public String getNext() {
        return this.next;
    }

    @Override
    public String getStartLimit() {
        return this.startLimit;
    }

    @Override
    public String getAllowStartIfComplete() {
        return this.allowStartIfComplete;
    }

    @Override
    public Listeners getListeners() {
        return this.listeners;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public List<Transition> getTransitions() {
        return this.transitions;
    }
}
