package io.machinecode.chainlink.jsl.core.inherit.execution;

import io.machinecode.chainlink.jsl.core.inherit.Util;
import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.loader.JobRepository;
import io.machinecode.chainlink.spi.Mergeable;
import io.machinecode.chainlink.spi.element.Properties;
import io.machinecode.chainlink.spi.element.execution.Decision;
import io.machinecode.chainlink.spi.element.transition.Transition;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritableDecision<T extends InheritableDecision<T, U, V>,
        U extends Mergeable<U> & Properties,
        V extends Copyable & Transition>
        extends InheritableExecution<T>, Decision {

    T setId(final String id);

    T setRef(final String ref) ;

    @Override
    U getProperties();

    T setProperties(final U properties);

    @Override
    List<V> getTransitions();

    T setTransitions(final List<V> transitions);

    /**
     * @author Brent Douglas <brent.n.douglas@gmail.com>
     */
    class DecisionTool {

        public static <T extends InheritableDecision<T, U, V>,
                U extends Mergeable<U> & Properties,
                V extends Copyable & Transition>
        T inherit(final T _this, final JobRepository repository, final String defaultJobXml) {
            return _this.copy();
        }

        public static <T extends InheritableDecision<T, U, V>,
                U extends Mergeable<U> & Properties,
                V extends Copyable & Transition>
        T copy(final T _this, final T that) {
            that.setId(_this.getId());
            that.setRef(_this.getRef());
            that.setProperties(Util.copy(_this.getProperties()));
            that.setTransitions(Util.copyList(_this.getTransitions()));
            return that;
        }
    }
}
