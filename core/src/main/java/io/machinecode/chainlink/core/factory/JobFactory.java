package io.machinecode.chainlink.core.factory;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.factory.execution.Executions;
import io.machinecode.chainlink.core.jsl.impl.JobImpl;
import io.machinecode.chainlink.core.jsl.impl.ListenersImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.execution.ExecutionImpl;
import io.machinecode.chainlink.core.property.SystemPropertyLookup;
import io.machinecode.chainlink.core.validation.InvalidJobException;
import io.machinecode.chainlink.core.validation.JobValidator;
import io.machinecode.chainlink.spi.jsl.Job;

import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobFactory {

    public static JobImpl produce(final Job that, final Properties parameters) throws InvalidJobException {
        return produce(that, parameters, SystemPropertyLookup.INSTANCE);
    }

    public static JobImpl produce(final Job that, final Properties parameters, final SystemPropertyLookup lookup) throws InvalidJobException {
        JobValidator.assertValid(that);
        final JobPropertyContext context = new JobPropertyContext(parameters, lookup);

        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String version = Expression.resolveExecutionProperty(that.getVersion(), context);
        final String restartable = Expression.resolveExecutionProperty(that.getRestartable(), context);
        final PropertiesImpl properties = PropertiesFactory.produceExecution(that.getProperties(), context);
        final ListenersImpl listeners = JobListenersFactory.produceExecution(that.getListeners(), context);
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
}
