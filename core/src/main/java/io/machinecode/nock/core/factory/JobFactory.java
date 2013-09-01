package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContextImpl;
import io.machinecode.nock.core.expression.PropertyContextImpl;
import io.machinecode.nock.core.expression.PropertyContextImpl;
import io.machinecode.nock.core.factory.execution.Executions;
import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.execution.ExecutionImpl;
import io.machinecode.nock.jsl.validation.InvalidJobException;
import io.machinecode.nock.jsl.validation.InvalidTransitionException;
import io.machinecode.nock.jsl.validation.JobValidator;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.Job;

import javax.batch.api.listener.JobListener;
import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobFactory {

    public static final JobFactory INSTANCE = new JobFactory();

    public JobImpl produceExecution(final Job that, final Properties parameters) {
        final VisitorNode before = JobValidator.INSTANCE.visit(that);
        if (JobValidator.hasFailed(before)) {
            throw new InvalidJobException(before);
        }
        final JobPropertyContextImpl context = new JobPropertyContextImpl(parameters);

        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String version = Expression.resolveExecutionProperty(that.getVersion(), context);
        final String restartable = Expression.resolveExecutionProperty(that.getRestartable(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        final ListenersImpl listeners = JobListenersFactory.INSTANCE.produceExecution(that.getListeners(), context);
        final List<ExecutionImpl> executions = Executions.immutableCopyExecutionsDescriptor(that.getExecutions(), context);
        return new JobImpl(
                id,
                version,
                restartable,
                properties,
                listeners,
                executions
        );
    }

    public VisitorNode validate(final Job job) {
        final VisitorNode node = JobValidator.INSTANCE.visit(job);
        boolean failed = JobValidator.hasCycle(node);
        failed = JobValidator.hasInvalidTransfer(node) || failed;
        if (failed) {
            throw new InvalidTransitionException(node);
        }
        return node;
    }

    public JobImpl producePartition(final JobImpl that) {
        final PropertyContextImpl context = new PropertyContextImpl();
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String version = Expression.resolvePartitionProperty(that.getVersion(), context);
        final String restartable = Expression.resolvePartitionProperty(that.getRestartable(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        final ListenersImpl listeners = JobListenersFactory.INSTANCE.producePartitioned(that.getListeners(), context);

        final List<ExecutionImpl> executions = Executions.immutableCopyExecutionsPartition(that.getExecutions(), context);
        return new JobImpl(
                id,
                version,
                restartable,
                properties,
                listeners,
                executions
        );
    }
}
