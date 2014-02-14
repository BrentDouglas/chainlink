package io.machinecode.nock.jsl.validation.execution;

import io.machinecode.nock.jsl.validation.transition.TransitionValidator;
import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.execution.Decision;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.element.transition.Transition;
import io.machinecode.nock.spi.util.Messages;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FlowValidator extends ValidatingVisitor<Flow> {

    public static final FlowValidator INSTANCE = new FlowValidator();

    protected FlowValidator() {
        super(Flow.ELEMENT);
    }

    @Override
    public void doVisit(final Flow that, final VisitorNode context) {
        if (that.getId() == null) {
            context.addProblem(Messages.format("NOCK-002102.validation.required.attribute", "id"));
        } else {
            context.addTransition(Messages.get("NOCK-002301.validation.next.attribute"), that.getNext());
        }

        if (that.getTransitions() != null) {
            for (final Transition transition : that.getTransitions()) {
                if (transition == null) {
                    context.addProblem(Messages.format("NOCK-002100.validation.not.null.element", "transitions"));
                }
                TransitionValidator.visit(transition, context);
            }
        }


        if (that.getExecutions() == null || that.getExecutions().isEmpty()) {
            //TODO Surely this is a thing
            //context.addProblem(Messages.get("validation.executions.required"));
        } else {
            if (that.getExecutions().get(0) instanceof Decision) {
                context.addProblem(Messages.format("NOCK-002101.validation.decision.first.execution"));
            }
            context.addChildTransition(Messages.get("NOCK-002302.validation.flow.implicit"), that.getExecutions().get(0).getId());
            for (final Execution execution : that.getExecutions()) {
                if (execution == null) {
                    context.addProblem(Messages.format("NOCK-002100.validation.not.null.element", "execution"));
                    continue;
                }
                ExcecutionValidator.visit(execution, context);
            }
        }
    }
}
