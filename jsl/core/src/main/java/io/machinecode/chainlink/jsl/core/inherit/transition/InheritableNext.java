package io.machinecode.chainlink.jsl.core.inherit.transition;

import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.element.transition.Next;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface InheritableNext<T extends InheritableNext<T>>
        extends Copyable<T>, Next {

    T setOn(final String on);

    T setTo(final String to);

    class NextTool {

        public static <T extends InheritableNext<T>>
        T copy(final T _this, final T that) {
            that.setOn(_this.getOn());
            that.setTo(_this.getTo());
            return that;
        }
    }
}
