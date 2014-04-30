package io.machinecode.chainlink.core.element.execution;

import io.machinecode.chainlink.core.inject.InjectablesImpl;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.element.PropertiesImpl;
import io.machinecode.chainlink.core.element.transition.TransitionImpl;
import io.machinecode.chainlink.core.util.Statuses;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.MutableJobContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.element.execution.Decision;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.TransitionWork;
import io.machinecode.chainlink.spi.then.Chain;
import org.jboss.logging.Logger;

import javax.batch.api.Decider;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.StepExecution;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionImpl extends ExecutionImpl implements Decision {

    private static final Logger log = Logger.getLogger(DecisionImpl.class);

    public static final long[] NO_STEPS = new long[0];
    public static final StepExecution[] NO_STEP_EXECUTIONS = new StepExecution[0];

    private final List<TransitionImpl> transitions;
    private final PropertiesImpl properties;
    private final ArtifactReferenceImpl ref;

    protected transient Decider _decider;

    public DecisionImpl(final String id, final ArtifactReferenceImpl ref, final PropertiesImpl properties,
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

    // Lifecycle

    @Override
    public Chain<?> before(final RuntimeConfiguration configuration, final ExecutionRepositoryId executionRepositoryId,
                              final WorkerId workerId, final ExecutableId callbackId, final ExecutableId parentId,
                              final ExecutionContext context) throws Exception {
        log.debugf(Messages.get("CHAINLINK-019000.decision.before"), context, this.id);
        final long[] prior = context.getPriorStepExecutionIds();
        final Long last = context.getLastStepExecutionId();
        final long[] actual = prior.length != 0 ? prior : last != null ? new long[]{ last } : NO_STEPS;
        log.debugf(Messages.get("CHAINLINK-019002.decision.decide"), context, this.id, this.ref.ref());
        final String exitStatus = decide(
                configuration,
                context,
                actual == NO_STEPS
                        ? NO_STEP_EXECUTIONS
                        : configuration.getExecutionRepository(executionRepositoryId).getStepExecutions(actual)
        );
        context.getJobContext().setExitStatus(exitStatus);
        return runCallback(configuration, context, callbackId);
    }

    @Override
    public Chain<?> after(final RuntimeConfiguration configuration, final ExecutionRepositoryId executionRepositoryId,
                             final WorkerId workerId, final ExecutableId parentId, final ExecutionContext context,
                             final ExecutionContext childContext) throws Exception {
        log.debugf(Messages.get("CHAINLINK-019001.decision.after"), context, this.id);
        final MutableJobContext jobContext = context.getJobContext();
        final BatchStatus batchStatus = jobContext.getBatchStatus();
        if (Statuses.isStopping(batchStatus) || Statuses.isFailed(batchStatus)) {
            return runCallback(configuration, context, parentId);
        }
        final TransitionWork transition = this.transition(context, this.transitions, jobContext.getBatchStatus(), jobContext.getExitStatus());
        if (transition != null && transition.isTerminating()) {
            return runCallback(configuration, context, parentId);
        } else {
            return this.next(configuration, workerId, context, parentId, executionRepositoryId, null, transition);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }

    public Decider load(final InjectionContext injectionContext, final ExecutionContext context) throws Exception {
        if (this._decider != null) {
            return this._decider;
        }
        final Decider that = this.ref.load(Decider.class, injectionContext, context);
        if (that == null) {
            throw new IllegalStateException(Messages.format("CHAINLINK-025004.artifact.null", context, this.ref));
        }
        this._decider = that;
        return that;
    }

    public String decide(final RuntimeConfiguration configuration, final ExecutionContext context, final StepExecution[] executions) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(new InjectablesImpl(
                    context.getJobContext(),
                    context.getStepContext(),
                    properties.getProperties()
            ));
            final Decider decider = load(injectionContext, context);
            if (decider == null) {
                throw new IllegalStateException(Messages.format("CHAINLINK-025004.artifact.null", context, this.ref.ref()));
            }
            return decider.decide(executions);
        } finally {
            provider.setInjectables(null);
        }
    }
}
