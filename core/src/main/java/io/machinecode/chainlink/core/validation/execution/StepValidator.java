package io.machinecode.chainlink.core.validation.execution;

import io.machinecode.chainlink.core.validation.ListenersValidator;
import io.machinecode.chainlink.core.validation.partition.PartitionValidator;
import io.machinecode.chainlink.core.validation.task.TaskValidator;
import io.machinecode.chainlink.core.validation.transition.TransitionValidator;
import io.machinecode.chainlink.core.validation.PropertiesValidator;
import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.element.execution.Step;
import io.machinecode.chainlink.spi.element.transition.Transition;
import io.machinecode.chainlink.spi.util.Messages;

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
            context.addProblem(Messages.format("CHAINLINK-002102.validation.required.attribute", "id"));
        } else {
            context.addTransition(Messages.get("CHAINLINK-002301.validation.next.attribute"), that.getNext());
        }
        //if (that.getStartLimit() < 0) {
        //    context.addProblem(Problem.attributePositive("start-limit", that.getStartLimit()));
        //}

        if (that.getTransitions() != null) {
            for (final Object transition : that.getTransitions()) {
                if (transition == null) {
                    context.addProblem(Messages.format("CHAINLINK-002100.validation.not.null.element", "transition"));
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
