package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.execution.Executions;
import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.jsl.api.Job;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.validation.JobValidator;
import io.machinecode.nock.jsl.validation.TransitionCrawler;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobFactory {

    public static final JobFactory INSTANCE = new JobFactory();

    public JobImpl produceBuildTime(final Job that, final Properties parameters) {
        JobValidator.INSTANCE.validate(that);
        final JobPropertyContext context = new JobPropertyContext(parameters);

        final String id = Expression.resolveBuildTime(that.getId(), context);
        final String version = Expression.resolveBuildTime(that.getVersion(), context);
        final String restartable = Expression.resolveBuildTime(that.isRestartable(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceBuildTime(that.getProperties(), context);
        final ListenersImpl listeners = ListenersFactory.INSTANCE.produceBuildTime(that.getListeners(), context);
        final List<Execution> executions = Executions.immutableCopyExecutionsBuildTime(that.getExecutions(), context);
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

    public JobImpl producePartitionTime(final Job that) {
        final PartitionPropertyContext context = new PartitionPropertyContext();

        final List<Execution> executions = Executions.immutableCopyExecutionsPartitionTime(that.getExecutions(), context);
        return new JobImpl(
                that.getId(),
                that.getVersion(),
                that.isRestartable(),
                that.getProperties(),
                that.getListeners(),
                executions
        );
    }
}
