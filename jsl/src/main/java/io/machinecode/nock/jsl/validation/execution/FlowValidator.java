package io.machinecode.nock.jsl.validation.execution;

import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.api.transition.Transition;
import io.machinecode.nock.jsl.validation.ValidationContext;
import io.machinecode.nock.jsl.validation.Validator;
import io.machinecode.nock.jsl.validation.transition.TransitionValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FlowValidator extends Validator<Flow> {

    public static final FlowValidator INSTANCE = new FlowValidator();

    protected FlowValidator() {
        super("flow");
    }

    @Override
    public void doValidate(final Flow that, final ValidationContext context) {
        if (that.getId() == null) {
            context.addProblem("Attribute 'id' is required.");
        }

        if (that.getTransitions() != null) {
            for (final Transition transition : that.getTransitions()) {
                if (transition == null) {
                    context.addProblem("Must not have null 'transitions' element.");
                }
                TransitionValidator.validate(transition, context);
            }
        }

        if (that.getExecutions() != null) {
            for (final Execution execution : that.getExecutions()) {
                if (execution == null) {
                    context.addProblem("Mut not have null 'executions' element.");
                }
                ExcecutionValidator.validate(execution, context);
            }
        }
    }
}
