package io.machinecode.nock.jsl.validation.execution;

import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.element.transition.Transition;
import io.machinecode.nock.jsl.validation.Problem;
import io.machinecode.nock.jsl.validation.ValidationContext;
import io.machinecode.nock.jsl.validation.Validator;
import io.machinecode.nock.jsl.validation.transition.TransitionValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FlowValidator extends Validator<Flow> {

    public static final FlowValidator INSTANCE = new FlowValidator();

    protected FlowValidator() {
        super(Flow.ELEMENT);
    }

    @Override
    public void doValidate(final Flow that, final ValidationContext context) {
        if (that.getId() == null) {
            context.addProblem(Problem.attributeRequired("id"));
        } else {
            context.addId(that.getId());
        }

        if (that.getTransitions() != null) {
            for (final Transition transition : that.getTransitions()) {
                if (transition == null) {
                    context.addProblem(Problem.notNullElement("transitions"));
                }
                TransitionValidator.validate(transition, context);
            }
        }

        if (that.getExecutions() != null) {
            for (final Execution execution : that.getExecutions()) {
                if (execution == null) {
                    context.addProblem(Problem.notNullElement("executions"));
                }
                ExcecutionValidator.validate(execution, context);
            }
        }
    }
}
