package io.machinecode.chainlink.jsl.core.inherit.execution;

import io.machinecode.chainlink.jsl.core.inherit.Util;
import io.machinecode.chainlink.jsl.core.inherit.InheritableBase;
import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.Inheritable;
import io.machinecode.chainlink.spi.loader.JobRepository;
import io.machinecode.chainlink.spi.element.execution.Execution;
import io.machinecode.chainlink.spi.element.execution.Flow;
import io.machinecode.chainlink.spi.element.transition.Transition;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableFlow<T extends InheritableFlow<T, U, V>,
        U extends Inheritable & Execution,
        V extends Copyable & Transition>
        extends InheritableBase<T>, InheritableExecution<T>, Flow {

    T setId(final String id);

    T setNext(final String next);

    @Override
    List<U> getExecutions();

    T setExecutions(final List<U> executions);

    @Override
    List<V> getTransitions();

    T setTransitions(final List<V> transitions);

    /**
     * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
     */
    class FlowTool {

        public static <T extends InheritableFlow<T, U, V>,
                U extends Inheritable & Execution,
                V extends Copyable & Transition>
        T inherit(final Class<T> clazz, final T _this, final JobRepository repository, final String defaultJobXml) {
            final T copy = _this.copy();
            if (_this.getParent() != null) {
                final T that = repository.findParent(clazz, copy, defaultJobXml);

                that.getTransitions().clear(); // 4.6.3.1 Drop parent transitions

                copy.getExecutions().clear(); // 4.6.3.2

                BaseTool.inheritingElementRule(copy, that); // 4.6.3.3

                // 4.6.3.4
                copy.setId(Util.attributeRule(copy.getId(), that.getId())); // 4.1
                copy.setNext(Util.attributeRule(copy.getNext(), that.getNext())); // 4.1

                copy.setExecutions(Util.inheritingList(repository, defaultJobXml, that.getExecutions()));
            } else {
                copy.setExecutions(Util.inheritingList(repository, defaultJobXml, _this.getExecutions()));
            }

            return copy;
        }

        public static <T extends InheritableFlow<T, U, V>,
                U extends Inheritable & Execution,
                V extends Copyable & Transition>
        T copy(final T _this, final T that) {
            BaseTool.copy(_this, that);
            that.setId(_this.getId());
            that.setNext(_this.getNext());
            that.setExecutions(Util.copyList(_this.getExecutions())); //TODO Skip these on inherit call
            that.setTransitions(Util.copyList(_this.getTransitions()));
            return that;
        }
    }
}
