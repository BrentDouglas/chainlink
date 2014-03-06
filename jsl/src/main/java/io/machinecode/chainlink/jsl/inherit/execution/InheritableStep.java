package io.machinecode.chainlink.jsl.inherit.execution;

import io.machinecode.chainlink.jsl.inherit.InheritableBase;
import io.machinecode.chainlink.jsl.inherit.Util;
import io.machinecode.chainlink.jsl.xml.task.XmlBatchlet;
import io.machinecode.chainlink.jsl.xml.task.XmlChunk;
import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.loader.JobRepository;
import io.machinecode.chainlink.spi.Mergeable;
import io.machinecode.chainlink.spi.element.Listeners;
import io.machinecode.chainlink.spi.element.Properties;
import io.machinecode.chainlink.spi.element.execution.Step;
import io.machinecode.chainlink.spi.element.partition.Partition;
import io.machinecode.chainlink.spi.element.task.Task;
import io.machinecode.chainlink.spi.element.transition.Transition;

import javax.batch.operations.JobStartException;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritableStep<T extends InheritableStep<T, P, L, M, X, Y>,
        P extends Mergeable<P> & Properties,
        L extends Mergeable<L> & Listeners,
        M extends Mergeable & Task,
        X extends Copyable & Transition,
        Y extends Copyable<Y> & Partition>
        extends InheritableBase<T>, InheritableExecution<T>, Step {

    T setId(final String id);

    T setNext(final String next);

    T setStartLimit(final String startLimit);

    T setAllowStartIfComplete(final String allowStartIfComplete);

    @Override
    P getProperties();

    T setProperties(final P properties);

    @Override
    L getListeners();

    T setListeners(final L listeners) ;

    @Override
    M getTask();

    T setTask(final M task);

    @Override
    List<X> getTransitions();

    T setTransitions(final List<X> transitions);

    @Override
    Y getPartition();

    T setPartition(final Y partition);

    class StepTool {

        public static <T extends InheritableStep<T, P, L, M, X, Y>,
                P extends Mergeable<P> & Properties,
                L extends Mergeable<L> & Listeners,
                M extends Mergeable & Task,
                X extends Copyable & Transition,
                Y extends Copyable<Y> & Partition>
        T inherit(final Class<T> clazz, final T _this, final JobRepository repository, final String defaultJobXml) {
            final T copy = _this.copy();
            if (copy.getParent() != null) {
                final T that = repository.findParent(clazz, copy, defaultJobXml);

                BaseTool.inheritingElementRule(copy, that); // 4.6.2.1

                copy.setPartition(Util.attributeRule(copy.getPartition(), that.getPartition())); // 4.6.2.2

                copy.setNext(Util.attributeRule(copy.getNext(), that.getNext())); // 4.1
                copy.setStartLimit(Util.attributeRule(copy.getStartLimit(), that.getStartLimit())); // 4.1
                copy.setAllowStartIfComplete(Util.attributeRule(copy.getAllowStartIfComplete(), that.getAllowStartIfComplete())); // 4.1

                // Skip types
                // 4.3
                copy.setProperties(Util.merge(copy.getProperties(), that.getProperties()));
                copy.setListeners(Util.merge(copy.getListeners(), that.getListeners()));
                copy.setTransitions(Util.listRule(copy.getTransitions(), that.getTransitions()));
                // 4.1
                if (copy.getTask() instanceof XmlChunk && that.getTask() instanceof XmlBatchlet
                        || copy.getTask() instanceof XmlBatchlet && that.getTask() instanceof XmlChunk) {
                    throw new JobStartException();
                }
                copy.setTask((M)Util.recursiveElementRule(copy.getTask(), that.getTask(), repository, defaultJobXml)); // 4.4.1
            }
            return copy;
        }

        public static <T extends InheritableStep<T, P, L, M, X, Y>,
                P extends Mergeable<P> & Properties,
                L extends Mergeable<L> & Listeners,
                M extends Mergeable & Task,
                X extends Copyable & Transition,
                Y extends Copyable<Y> & Partition>
        T copy(final T _this, final T that) {
            BaseTool.copy(_this, that);
            that.setId(_this.getId());
            that.setNext(_this.getNext());
            that.setStartLimit(_this.getStartLimit());
            that.setAllowStartIfComplete(_this.getAllowStartIfComplete());
            that.setProperties(Util.copy(_this.getProperties()));
            that.setListeners(Util.copy(_this.getListeners()));
            that.setTask((M)Util.copy(_this.getTask()));
            that.setPartition(Util.copy(_this.getPartition()));
            that.setTransitions(Util.copyList(_this.getTransitions()));
            return that;
        }
    }
}
