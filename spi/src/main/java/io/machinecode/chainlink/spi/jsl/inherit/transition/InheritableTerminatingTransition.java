package io.machinecode.chainlink.spi.jsl.inherit.transition;

import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.element.transition.TerminatingTransition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
