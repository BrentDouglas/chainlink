package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.execution.ExecutionImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.execution.Executions;
import io.machinecode.nock.jsl.validation.JobValidator;
import io.machinecode.nock.jsl.validation.TransitionCrawler;
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
        JobValidator.INSTANCE.validate(that);
        final JobPropertyContext context = new JobPropertyContext(parameters);

        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String version = Expression.resolveExecutionProperty(that.getVersion(), context);
        final String restartable = Expression.resolveExecutionProperty(that.isRestartable(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        final ListenersImpl<JobListener> listeners = JobListenersFactory.INSTANCE.produceExecution(that.getListeners(), context);
        final List<ExecutionImpl> executions = Executions.immutableCopyExecutionsDescriptor(that.getExecutions(), context);
        final JobImpl impl = new JobImpl(
                id,
                version,
                restartable,
                properties,
                listeners,
                executions
        );

        TransitionCrawler.validateTransitions(impl);
        return impl;
    }

    public JobImpl producePartition(final JobImpl that) {
        final PartitionPropertyContext context = new PartitionPropertyContext();
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String version = Expression.resolvePartitionProperty(that.getVersion(), context);
        final String restartable = Expression.resolvePartitionProperty(that.isRestartable(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        final ListenersImpl<JobListener> listeners = JobListenersFactory.INSTANCE.producePartitioned(that.getListeners(), context);

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
