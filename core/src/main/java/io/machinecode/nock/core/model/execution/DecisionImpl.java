package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.core.work.ExecutionExecutable;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.element.execution.Decision;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.TransitionWork;
import org.jboss.logging.Logger;

import javax.batch.api.Decider;
import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionImpl extends ExecutionImpl implements Decision {

    private static final Logger log = Logger.getLogger(DecisionImpl.class);

    private final List<TransitionImpl> transitions;
    private final PropertiesImpl properties;
    private final TypedArtifactReference<Decider> ref;

    public DecisionImpl(final String id, final TypedArtifactReference<Decider> ref, final PropertiesImpl properties,
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

    private transient String exitStatus;

    @Override
    public Deferred<?> before(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
                                 final CallbackExecutable parentExecutable, final ExecutionContext context,
                                 final ExecutionContext[] contexts) throws Exception {
        log.debugf(Messages.get("decision.decide"), context.getJobExecutionId(), this.id, this.ref.ref());
        final Decider decider = this.ref.load(executor, context, this);
        final long[] ids = new long[contexts.length];
        for (int i = 0; i < contexts.length; ++i) {
            ids[i] = contexts[i].getStepExecutionId();
        }
        this.exitStatus = decider.decide(
                executor.getRepository().getStepExecutions(ids)
        );
        return executor.callback(thisExecutable, context);
    }

    @Override
    public Deferred<?> after(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
                                final CallbackExecutable parentExecutable, final ExecutionContext context,
                                final ExecutionContext childContext) throws Exception {
        return this.transition(executor, threadId, context, parentExecutable, this.transitions, null, exitStatus);
    }
}
