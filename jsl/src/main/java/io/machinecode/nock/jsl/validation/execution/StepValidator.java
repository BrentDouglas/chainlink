package io.machinecode.nock.jsl.validation.execution;

import io.machinecode.nock.jsl.validation.ListenersValidator;
import io.machinecode.nock.jsl.validation.PropertiesValidator;
import io.machinecode.nock.jsl.validation.partition.PartitionValidator;
import io.machinecode.nock.jsl.validation.task.TaskValidator;
import io.machinecode.nock.jsl.validation.transition.TransitionValidator;
import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.execution.Step;
import io.machinecode.nock.spi.element.transition.Transition;
import io.machinecode.nock.spi.util.Messages;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepValidator extends ValidatingVisitor<Step> {

    public static final StepValidator INSTANCE = new StepValidator();

    protected StepValidator() {
        super(Step.ELEMENT);
    }

    @Override
    public void doVisit(final Step that, final VisitorNode context) {
        if (that.getId() == null) {
            context.addProblem(Messages.attributeRequired("id"));
        } else {
            context.setTransition(that.getId(), that.getNext());
        }
        //if (that.getStartLimit() < 0) {
        //    context.addProblem(Problem.attributePositive("start-limit", that.getStartLimit()));
        //}

        if (that.getTransitions() != null) {
            for (final Object transition : that.getTransitions()) {
                if (transition == null) {
                    context.addProblem(Messages.notNullElement("transition"));
                }
                TransitionValidator.visit((Transition) transition, context);
            }
        }

        if (that.getTask() != null) {
            TaskValidator.validate(that.getTask(), context);
        }
        if (that.getPartition() != null) {
            PartitionValidator.INSTANCE.visit(that.getPartition(), context);
        }
        if (that.getListeners() != null) {
            ListenersValidator.INSTANCE.visit(that.getListeners(), context);
        }
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.visit(that.getProperties(), context);
        }
        //TODO Traverse that.next for cycles
    }
}
