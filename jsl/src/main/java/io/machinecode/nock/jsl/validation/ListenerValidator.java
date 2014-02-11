package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.util.Messages;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenerValidator extends ValidatingVisitor<Listener> {

    public static final ListenerValidator INSTANCE = new ListenerValidator();

    protected ListenerValidator() {
        super(Listener.ELEMENT);
    }

    @Override
    public void doVisit(final Listener that, final VisitorNode context) {
        if (that.getRef() == null) {
            context.addProblem(Messages.format("validation.required.attribute", "ref"));
        }
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.visit(that.getProperties(), context);
        }
    }
}
