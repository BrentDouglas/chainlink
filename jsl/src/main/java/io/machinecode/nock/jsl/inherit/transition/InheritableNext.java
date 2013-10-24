package io.machinecode.nock.jsl.inherit.transition;

import io.machinecode.nock.spi.Copyable;
import io.machinecode.nock.spi.element.transition.Next;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
