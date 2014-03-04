package io.machinecode.chainlink.jsl.validation.transition;

import io.machinecode.chainlink.jsl.visitor.ValidatingVisitor;
import io.machinecode.chainlink.jsl.visitor.VisitorNode;
import io.machinecode.chainlink.spi.element.transition.Fail;
import io.machinecode.chainlink.spi.util.Messages;

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
            context.addProblem(Messages.format("CHAINLINK-002102.validation.required.attribute", "on"));
        }
    }
}
