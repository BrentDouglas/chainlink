package io.machinecode.chainlink.core.jsl.impl.execution;

import io.machinecode.chainlink.core.inject.InjectablesImpl;
import io.machinecode.chainlink.core.jsl.impl.JobImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertyReferenceImpl;
import io.machinecode.chainlink.core.jsl.impl.transition.TransitionImpl;
import io.machinecode.chainlink.core.util.Repo;
import io.machinecode.chainlink.core.util.Statuses;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.MutableJobContext;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.inject.ArtifactReference;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.jsl.execution.Decision;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import javax.batch.api.Decider;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.StepExecution;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DecisionImpl extends ExecutionImpl implements Decision {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(DecisionImpl.class);

    public static final long[] NO_STEPS = new long[0];
    public static final StepExecution[] NO_STEP_EXECUTIONS = new StepExecution[0];

    private final List<TransitionImpl> transitions;
    private final PropertiesImpl properties;
    private final ArtifactReference ref;

    public DecisionImpl(final String id, final ArtifactReference ref, final PropertiesImpl properties,
                        final List<TransitionImpl> transitions) {
        super(id);
        this.transitions = transitions;
        this.properties = properties;
        this.ref = ref;
    }

    @Override
    public List<TransitionImpl> getTransitions() {
        return this.transitions;
    }

    @Override
    public String getRef() {
        return this.ref.ref();
    }

    @Override
    public PropertiesImpl getProperties() {
        return this.properties;
    }

    @Override
    public Promise<Chain<?>,Throwable,?> before(final JobImpl job, final Configuration configuration, final RepositoryId repositoryId,
                           final WorkerId workerId, final ExecutableId callbackId, final ExecutableId parentId,
                           final ExecutionContext context) throws Exception {
        log.debugf(Messages.get("CHAINLINK-019000.decision.before"), context, this.id);
        final long[] prior = context.getPriorStepExecutionIds();
        final Long last = context.getLastStepExecutionId();
        final long[] actual = (prior != null && prior.length != 0) ? prior : last != null ? new long[]{ last } : NO_STEPS;
        log.debugf(Messages.get("CHAINLINK-019002.decision.decide"), context, this.id, this.ref.ref());
        final String exitStatus = decide(
                configuration,
                context,
                actual == NO_STEPS
                        ? NO_STEP_EXECUTIONS
                        : Repo.getRepository(configuration, repositoryId).getStepExecutions(actual)
        );
        context.getJobContext().setExitStatus(exitStatus);
        return configuration.getTransport().callback(callbackId, context);
    }

    @Override
    public Promise<Chain<?>,Throwable,?> after(final JobImpl job, final Configuration configuration, final RepositoryId repositoryId,
                          final WorkerId workerId, final ExecutableId parentId, final ExecutionContext context,
                          final ExecutionContext childContext) throws Exception {
        log.debugf(Messages.get("CHAINLINK-019001.decision.after"), context, this.id);
        final MutableJobContext jobContext = context.getJobContext();
        final BatchStatus batchStatus = jobContext.getBatchStatus();
        if (Statuses.isStopping(batchStatus) || Statuses.isFailed(batchStatus)) {
            return configuration.getTransport().callback(parentId, context);
        }
        final TransitionImpl transition = this.transition(context, this.transitions, jobContext.getBatchStatus(), jobContext.getExitStatus());
        if (transition != null && transition.isTerminating()) {
            return configuration.getTransport().callback(parentId, context);
        } else {
            return this.next(job, configuration, workerId, context, parentId, repositoryId, null, transition);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }

    public String decide(final Configuration configuration, final ExecutionContext context, final StepExecution[] executions) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(new InjectablesImpl(
                    context.getJobContext(),
                    context.getStepContext(),
                    properties.getProperties()
            ));
            return PropertyReferenceImpl.load(this.ref, Decider.class, configuration, context).decide(executions);
        } finally {
            provider.setInjectables(null);
        }
    }
}
