package io.machinecode.chainlink.core.validation.transition;

import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.element.transition.Fail;
import io.machinecode.chainlink.spi.util.Messages;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
