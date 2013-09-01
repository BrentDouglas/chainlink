package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.Listeners;
import io.machinecode.nock.spi.util.Message;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenersValidator extends ValidatingVisitor<Listeners> {

    public static final ListenersValidator INSTANCE = new ListenersValidator();

    protected ListenersValidator() {
        super(Listeners.ELEMENT);
    }

    @Override
    public void doVisit(final Listeners that, final VisitorNode context) {
        for(final Listener listener : that.getListeners()) {
            if (listener == null) {
                context.addProblem(Message.notNullElement("listener"));
                continue;
            }
            ListenerValidator.INSTANCE.visit(listener, context);
        }
    }
}
