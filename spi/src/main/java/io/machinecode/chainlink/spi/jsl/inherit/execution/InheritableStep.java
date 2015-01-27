package io.machinecode.chainlink.spi.jsl.inherit.execution;

import io.machinecode.chainlink.spi.jsl.inherit.Copyable;
import io.machinecode.chainlink.spi.jsl.inherit.Mergeable;
import io.machinecode.chainlink.spi.jsl.Listeners;
import io.machinecode.chainlink.spi.jsl.Properties;
import io.machinecode.chainlink.spi.jsl.execution.Step;
import io.machinecode.chainlink.spi.jsl.partition.Partition;
import io.machinecode.chainlink.spi.jsl.task.Batchlet;
import io.machinecode.chainlink.spi.jsl.task.Chunk;
import io.machinecode.chainlink.spi.jsl.task.Task;
import io.machinecode.chainlink.spi.jsl.transition.Transition;
import io.machinecode.chainlink.spi.jsl.inherit.InheritableBase;
import io.machinecode.chainlink.spi.jsl.inherit.Rules;
import io.machinecode.chainlink.spi.loader.JobRepository;
import io.machinecode.chainlink.spi.Messages;

import javax.batch.operations.JobStartException;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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

                copy.setPartition(Rules.attributeRule(copy.getPartition(), that.getPartition())); // 4.6.2.2

                copy.setNext(Rules.attributeRule(copy.getNext(), that.getNext())); // 4.1
                copy.setStartLimit(Rules.attributeRule(copy.getStartLimit(), that.getStartLimit())); // 4.1
                copy.setAllowStartIfComplete(Rules.attributeRule(copy.getAllowStartIfComplete(), that.getAllowStartIfComplete())); // 4.1

                // Skip types
                // 4.3
                copy.setProperties(Rules.merge(copy.getProperties(), that.getProperties()));
                copy.setListeners(Rules.merge(copy.getListeners(), that.getListeners()));
                copy.setTransitions(Rules.listRule(copy.getTransitions(), that.getTransitions()));
                // 4.1
                if (copy.getTask() instanceof Chunk && that.getTask() instanceof Batchlet
                        || copy.getTask() instanceof Batchlet && that.getTask() instanceof Chunk) {
                    throw new JobStartException(Messages.format("CHAINLINK-002400.validation.inheriting.step.different.tasks", copy.getId(), copy.getTask().getClass().getSimpleName(), that.getId(), that.getTask().getClass().getSimpleName()));
                }
                copy.setTask((M) Rules.recursiveElementRule(copy.getTask(), that.getTask(), repository, defaultJobXml)); // 4.4.1
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
            that.setProperties(Rules.copy(_this.getProperties()));
            that.setListeners(Rules.copy(_this.getListeners()));
            that.setTask((M) Rules.copy(_this.getTask()));
            that.setPartition(Rules.copy(_this.getPartition()));
            that.setTransitions(Rules.copyList(_this.getTransitions()));
            return that;
        }
    }
}
