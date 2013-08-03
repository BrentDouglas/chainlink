package io.machinecode.nock.jsl.validation.execution;

import io.machinecode.nock.jsl.api.execution.Decision;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.api.execution.Split;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.validation.ValidationContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class ExcecutionValidator {

    private ExcecutionValidator(){}

    public static void validate(final Execution that, final ValidationContext context) {
        if (that instanceof Split) {
            SplitValidator.INSTANCE.validate((Split) that, context);
        } else if (that instanceof Decision) {
            DecisionValidator.INSTANCE.validate((Decision) that, context);
        } else if (that instanceof Step) {
            StepValidator.INSTANCE.validate((Step) that, context);
        } else if (that instanceof Flow) {
            FlowValidator.INSTANCE.validate((Flow) that, context);
        }
    }
}
