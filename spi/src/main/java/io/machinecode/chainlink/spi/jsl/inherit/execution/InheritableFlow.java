package io.machinecode.chainlink.spi.jsl.inherit.execution;

import io.machinecode.chainlink.spi.jsl.execution.Execution;
import io.machinecode.chainlink.spi.jsl.execution.Flow;
import io.machinecode.chainlink.spi.jsl.inherit.Copyable;
import io.machinecode.chainlink.spi.jsl.inherit.Inheritable;
import io.machinecode.chainlink.spi.jsl.inherit.InheritableBase;
import io.machinecode.chainlink.spi.jsl.inherit.Rules;
import io.machinecode.chainlink.spi.jsl.transition.Transition;
import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

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
        T inherit(final Class<T> clazz, final T _this, final InheritableJobLoader repository, final String defaultJobXml) {
            final T copy = _this.copy();
            if (_this.getParent() != null) {
                final T that = repository.findParent(clazz, copy, defaultJobXml);

                that.getTransitions().clear(); // 4.6.3.1 Drop parent transitions

                copy.getExecutions().clear(); // 4.6.3.2

                BaseTool.inheritingElementRule(copy, that); // 4.6.3.3

                // 4.6.3.4
                copy.setId(Rules.attributeRule(copy.getId(), that.getId())); // 4.1
                copy.setNext(Rules.attributeRule(copy.getNext(), that.getNext())); // 4.1

                copy.setExecutions(Rules.inheritingList(repository, defaultJobXml, that.getExecutions()));
            } else {
                copy.setExecutions(Rules.inheritingList(repository, defaultJobXml, _this.getExecutions()));
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
            that.setExecutions(Rules.copyList(_this.getExecutions())); //TODO Skip these on inherit call
            that.setTransitions(Rules.copyList(_this.getTransitions()));
            return that;
        }
    }
}
