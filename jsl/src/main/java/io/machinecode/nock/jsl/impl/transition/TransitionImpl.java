package io.machinecode.nock.jsl.impl.transition;

import io.machinecode.nock.jsl.api.transition.End;
import io.machinecode.nock.jsl.api.transition.Fail;
import io.machinecode.nock.jsl.api.transition.Next;
import io.machinecode.nock.jsl.api.transition.Stop;
import io.machinecode.nock.jsl.api.transition.Transition;
import io.machinecode.nock.jsl.impl.Util;
import io.machinecode.nock.jsl.impl.Util.Transformer;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class TransitionImpl implements Transition {

    private static final Transformer<Transition> TRANSITION_TRANSFORMER = new Transformer<Transition>() {
        @Override
        public Transition transform(final Transition that) {
            if (that instanceof End) {
                return new EndImpl((End) that);
            } else if (that instanceof Fail) {
                return new FailImpl((Fail) that);
            } else if (that instanceof Next) {
                return new NextImpl((Next) that);
            } else if (that instanceof Stop) {
                return new StopImpl((Stop) that);
            }
            return null;
        }
    };

    private final String on;

    public TransitionImpl(final Transition that) {
        this.on = that.getOn();
    }

    @Override
    public String getOn() {
        return this.on;
    }

    public static List<Transition> immutableCopyTransitions(final List<? extends Transition> that) {
        return Util.immutableCopy(that, TRANSITION_TRANSFORMER);
    }

}
