package io.machinecode.nock.jsl.inherit.execution;

import io.machinecode.nock.jsl.inherit.Util;
import io.machinecode.nock.spi.Copyable;
import io.machinecode.nock.spi.JobRepository;
import io.machinecode.nock.spi.Mergeable;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.execution.Decision;
import io.machinecode.nock.spi.element.transition.Transition;

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
