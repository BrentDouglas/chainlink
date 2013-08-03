package io.machinecode.nock.jsl.validation.execution;

import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.api.execution.Split;
import io.machinecode.nock.jsl.validation.Problem;
import io.machinecode.nock.jsl.validation.ValidationContext;
import io.machinecode.nock.jsl.validation.Validator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SplitValidator extends Validator<Split> {

    public static final SplitValidator INSTANCE = new SplitValidator();

    protected SplitValidator() {
        super("split");
    }

    @Override
    public void doValidate(final Split that, final ValidationContext context) {
        if (that.getId() == null) {
            context.addProblem(Problem.attributeRequired("id"));
        } else {
            context.addId(that.getId());
        }

        if (that.getFlows() != null) {
            for (final Flow flow : that.getFlows()) {
                if (flow == null) {
                    context.addProblem(Problem.notNullElement("flow"));
                }
                FlowValidator.INSTANCE.validate(flow, context);
            }
        }
    }
}
