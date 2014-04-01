package io.machinecode.chainlink.jsl.core.inherit.transition;

import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.element.transition.TerminatingTransition;

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