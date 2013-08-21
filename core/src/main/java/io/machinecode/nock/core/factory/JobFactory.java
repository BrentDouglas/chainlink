package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.execution.Executions;
import io.machinecode.nock.core.descriptor.JobImpl;
import io.machinecode.nock.core.descriptor.ListenersImpl;
import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.JobWork;
import io.machinecode.nock.core.work.ListenersWork;
import io.machinecode.nock.core.work.execution.ExecutionWork;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.jsl.validation.JobValidator;
import io.machinecode.nock.jsl.validation.TransitionCrawler;

import javax.batch.api.listener.JobListener;
import javax.batch.operations.JobStartException;
import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobFactory {

    public static final JobFactory INSTANCE = new JobFactory();

    public JobImpl produceDescriptor(final Job that) {
        JobValidator.INSTANCE.validate(that);
        final JobPropertyContext context = new JobPropertyContext();

        final String id = Expression.resolveDescriptorProperty(that.getId(), context);
        final String version = Expression.resolveDescriptorProperty(that.getVersion(), context);
        final String restartable = Expression.resolveDescriptorProperty(that.isRestartable(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        final ListenersImpl listeners = JobListenersFactory.INSTANCE.produceDescriptor(that.getListeners(), context);
        final List<Execution> executions = Executions.immutableCopyExecutionsDescriptor(that.getExecutions(), context);
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

    public JobWork produceExecution(final JobImpl that, final Properties parameters) {
        final JobParameterContext context = new JobParameterContext(parameters);

        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String version = Expression.resolveExecutionProperty(that.getVersion(), context);
        final String restartable = Expression.resolveExecutionProperty(that.isRestartable(), context);
        final ListenersWork<JobListener> listeners = JobListenersFactory.INSTANCE.produceExecution(that.getListeners(), context);
        final List<ExecutionWork> executions = Executions.immutableCopyExecutionsExecution(that.getExecutions(), context);
        return new JobWork(
                id,
                version,
                restartable,
                listeners,
                executions
        );
    }

    public JobWork producePartition(final JobWork that) {
        final PartitionPropertyContext context = new PartitionPropertyContext();
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String version = Expression.resolvePartitionProperty(that.getVersion(), context);
        final String restartable = Expression.resolvePartitionProperty(that.isRestartable(), context);
        final ListenersWork<JobListener> listeners = JobListenersFactory.INSTANCE.producePartitioned(that.getListeners(), context);

        final List<ExecutionWork> executions = Executions.immutableCopyExecutionsPartition(that.getExecutions(), context);
        return new JobWork(
                id,
                version,
                restartable,
                listeners,
                executions
        );
    }
}
