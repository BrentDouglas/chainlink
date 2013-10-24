package io.machinecode.nock.jsl.inherit;

import io.machinecode.nock.spi.Copyable;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.Listeners;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritableListeners<T extends InheritableListeners<T, L>,
        L extends Copyable<L> & Listener>
        extends MergeableList<T>, Listeners {

    @Override
    List<L> getListeners();

    T setListeners(final List<L> listeners);

    class ListenersTool {

        public static <T extends InheritableListeners<T, L>,
                L extends Copyable<L> & Listener>
        T copy(final T _this, final T that) {
            that.setListeners(Util.copyList(_this.getListeners()));
            return that;
        }

        public static <T extends InheritableListeners<T, L>,
                L extends Copyable<L> & Listener>
        T merge(final T _this, final T that) {
            if (_this.getMerge()) {
                _this.setListeners(Util.listRule(_this.getListeners(), that.getListeners()));
            }
            return _this;
        }
    }
}
