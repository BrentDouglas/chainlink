package io.machinecode.nock.jsl.validation.transition;

import io.machinecode.nock.jsl.api.transition.Next;
import io.machinecode.nock.jsl.validation.Problem;
import io.machinecode.nock.jsl.validation.ValidationContext;
import io.machinecode.nock.jsl.validation.Validator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NextValidator extends Validator<Next> {

    public static final NextValidator INSTANCE = new NextValidator();

    protected NextValidator() {
        super(Next.ELEMENT);
    }

    @Override
    public void doValidate(final Next that, final ValidationContext context) {
        if (that.getOn() == null) {
            context.addProblem(Problem.attributeRequired("on"));
        }
        if (that.getTo() == null) {
            context.addProblem(Problem.attributeRequired("to"));
        }
    }
}
