package io.machinecode.chainlink.core.validation.transition;

import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.element.transition.End;
import io.machinecode.chainlink.spi.util.Messages;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EndValidator extends ValidatingVisitor<End> {

    public static final EndValidator INSTANCE = new EndValidator();

    protected EndValidator() {
        super(End.ELEMENT);
    }

    @Override
    public void doVisit(final End that, final VisitorNode context) {
        if (that.getOn() == null) {
            context.addProblem(Messages.format("CHAINLINK-002102.validation.required.attribute", "on"));
        }
    }
}
