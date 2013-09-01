package io.machinecode.nock.jsl.validation.execution;

import io.machinecode.nock.jsl.validation.PropertyReferenceValidator;
import io.machinecode.nock.jsl.validation.transition.TransitionValidator;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.execution.Decision;
import io.machinecode.nock.spi.element.transition.Transition;
import io.machinecode.nock.spi.util.Message;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionValidator extends PropertyReferenceValidator<Decision> {

    public static final DecisionValidator INSTANCE = new DecisionValidator();

    protected DecisionValidator() {
        super(Decision.ELEMENT);
    }

    @Override
    public void doVisit(final Decision that, final VisitorNode context) {
        super.visit(that, context);
        if (that.getId() == null) {
            context.addProblem(Message.attributeRequired("id"));
        } else {
            context.setTransition(that.getId(), null);
        }

        if (that.getTransitions() != null) {
            for (final Transition transition : that.getTransitions()) {
                if (transition == null) {
                    context.addProblem(Message.notNullElement("transitions"));
                }
                TransitionValidator.visit(transition, context);
            }
        }
    }
}
