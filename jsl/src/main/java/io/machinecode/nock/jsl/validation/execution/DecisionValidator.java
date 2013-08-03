package io.machinecode.nock.jsl.validation.execution;

import io.machinecode.nock.jsl.api.execution.Decision;
import io.machinecode.nock.jsl.api.transition.Transition;
import io.machinecode.nock.jsl.validation.Problem;
import io.machinecode.nock.jsl.validation.PropertyReferenceValidator;
import io.machinecode.nock.jsl.validation.ValidationContext;
import io.machinecode.nock.jsl.validation.transition.TransitionValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionValidator extends PropertyReferenceValidator<Decision> {

    public static final DecisionValidator INSTANCE = new DecisionValidator();

    protected DecisionValidator() {
        super("decision");
    }

    @Override
    public void doValidate(final Decision that, final ValidationContext context) {
        super.validate(that, context);
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
    }
}
