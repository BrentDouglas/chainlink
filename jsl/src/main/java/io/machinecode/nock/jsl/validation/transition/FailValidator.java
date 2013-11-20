package io.machinecode.nock.jsl.validation.transition;

import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.transition.Fail;
import io.machinecode.nock.spi.util.Messages;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FailValidator extends ValidatingVisitor<Fail> {

    public static final FailValidator INSTANCE = new FailValidator();

    protected FailValidator() {
        super(Fail.ELEMENT);
    }

    @Override
    public void doVisit(final Fail that, final VisitorNode context) {
        if (that.getOn() == null) {
            context.addProblem(Messages.attributeRequired("on"));
        }
    }
}
