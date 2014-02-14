package io.machinecode.nock.jsl.validation.transition;

import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.transition.Stop;
import io.machinecode.nock.spi.util.Messages;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StopValidator extends ValidatingVisitor<Stop> {

    public static final StopValidator INSTANCE = new StopValidator();

    protected StopValidator() {
        super(Stop.ELEMENT);
    }

    @Override
    public void doVisit(final Stop that, final VisitorNode context) {
        if (that.getOn() == null) {
            context.addProblem(Messages.format("NOCK-002102.validation.required.attribute", "on"));
        }
        //TODO Validate restart is a valid execution id if it exists
    }
}
