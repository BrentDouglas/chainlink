package io.machinecode.nock.jsl.inherit.transition;

import io.machinecode.nock.spi.Copyable;
import io.machinecode.nock.spi.element.transition.TerminatingTransition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritableTerminatingTransition<T extends InheritableTerminatingTransition<T>>
        extends Copyable<T>, TerminatingTransition {

    T setOn(final String on);

    T setExitStatus(final String exitStatus);

    class TerminatingTransitionTool {

        public static <T extends InheritableTerminatingTransition<T>>
        T copy(final T _this, final T that) {
            that.setOn(_this.getOn());
            that.setExitStatus(_this.getExitStatus());
            return that;
        }
    }
}
