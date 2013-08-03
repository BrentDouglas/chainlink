package io.machinecode.nock.jsl.validation.transition;

import io.machinecode.nock.jsl.api.transition.End;
import io.machinecode.nock.jsl.validation.Problem;
import io.machinecode.nock.jsl.validation.ValidationContext;
import io.machinecode.nock.jsl.validation.Validator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EndValidator extends Validator<End> {

    public static final EndValidator INSTANCE = new EndValidator();

    protected EndValidator() {
        super("end");
    }

    @Override
    public void doValidate(final End that, final ValidationContext context) {
        if (that.getOn() == null) {
            context.addProblem(Problem.attributeRequired("on"));
        }
    }
}
