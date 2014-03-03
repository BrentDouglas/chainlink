package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.impl.InjectablesImpl;
import io.machinecode.nock.core.loader.ArtifactReferenceImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.core.work.Statuses;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.MutableJobContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.element.execution.Decision;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.TransitionWork;
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
    public Deferred<?> before(final Executor executor, final ThreadId threadId, final Executable thisCallback,
                              final Executable parentCallback, final ExecutionContext context) throws Exception {
        log.debugf(Messages.get("NOCK-019000.decision.before"), context, this.id);
        final long[] prior = context.getPriorStepExecutionIds();
        final Long last = context.getLastStepExecutionId();
        final long[] actual = prior.length != 0 ? prior : last != null ? new long[]{ last } : NO_STEPS;
        log.debugf(Messages.get("NOCK-019002.decision.decide"), context, this.id, this.ref.ref());
        final String exitStatus = decide(executor, context,
                actual == NO_STEPS ? NO_STEP_EXECUTIONS : executor.getRepository().getStepExecutions(actual)
        );
        context.getJobContext().setExitStatus(exitStatus);
        return executor.callback(thisCallback, context);
    }

    @Override
    public Deferred<?> after(final Executor executor, final ThreadId threadId, final Executable callback,
                             final ExecutionContext context, final ExecutionContext childContext) throws Exception {
        log.debugf(Messages.get("NOCK-019001.decision.after"), context, this.id);
        final MutableJobContext jobContext = context.getJobContext();
        final BatchStatus batchStatus = jobContext.getBatchStatus();
        if (Statuses.isStopping(batchStatus) || Statuses.isFailed(batchStatus)) {
            return runCallback(executor, context, callback);
        }
        final TransitionWork transition = this.transition(context, this.transitions, jobContext.getBatchStatus(), jobContext.getExitStatus());
        if (transition != null && transition.isTerminating()) {
            return runCallback(executor, context, callback);
        } else {
            return this.next(executor, threadId, context, callback, null, transition);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }

    protected transient Decider _cached;

    public Decider load(final InjectionContext injectionContext) throws Exception {
        if (this._cached != null) {
            return this._cached;
        }
        final Decider that = this.ref.load(Decider.class, injectionContext);
        this._cached = that;
        return that;
    }

    public String decide(final Executor executor, final ExecutionContext context, final StepExecution[] executions) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(new InjectablesImpl(
                    context.getJobContext(),
                    context.getStepContext(),
                    properties.getProperties()
            ));
            final Decider decider = load(injectionContext);
            if (decider == null) {
                throw new IllegalStateException(Messages.format("NOCK-025004.artifact.null", context, this.ref.ref()));
            }
            return decider.decide(executions);
        } finally {
            provider.setInjectables(null);
        }
    }
}
