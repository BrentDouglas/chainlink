package io.machinecode.chainlink.core.validation.transition;

import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.element.transition.Stop;
import io.machinecode.chainlink.spi.util.Messages;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class StopValidator extends ValidatingVisitor<Stop> {

    public static final StopValidator INSTANCE = new StopValidator();

    protected StopValidator() {
        super(Stop.ELEMENT);
    }

    @Override
    public void doVisit(final Stop that, final VisitorNode context) {
        if (that.getOn() == null) {
            context.addProblem(Messages.format("CHAINLINK-002102.validation.required.attribute", "on"));
        }
        //TODO Validate restart is a valid execution id if it exists
    }
}
