package io.machinecode.nock.jsl.validation.execution;

import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.execution.Decision;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.element.execution.Split;
import io.machinecode.nock.spi.element.execution.Step;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class ExcecutionValidator {

    private ExcecutionValidator(){}

    public static void visit(final Execution that, final VisitorNode context) {
        if (that instanceof Split) {
            SplitValidator.INSTANCE.visit((Split) that, context);
        } else if (that instanceof Decision) {
            DecisionValidator.INSTANCE.visit((Decision) that, context);
        } else if (that instanceof Step) {
            StepValidator.INSTANCE.visit((Step) that, context);
        } else if (that instanceof Flow) {
            FlowValidator.INSTANCE.visit((Flow) that, context);
        }
    }
}
