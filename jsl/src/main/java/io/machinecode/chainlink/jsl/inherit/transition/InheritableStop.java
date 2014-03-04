package io.machinecode.chainlink.jsl.inherit.transition;

import io.machinecode.chainlink.spi.element.transition.Stop;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritableStop<T extends InheritableStop<T>>
        extends InheritableTerminatingTransition<T>, Stop {

    T setRestart(final String restart);

    class StopTool {

        public static <T extends InheritableStop<T>>
        T copy(final T _this, final T that) {
            that.setRestart(_this.getRestart());
            return TerminatingTransitionTool.copy(_this, that);
        }
    }
}
