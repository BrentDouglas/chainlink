package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.api.Listener;
import io.machinecode.nock.jsl.api.Listeners;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenersValidator extends Validator<Listeners> {

    public static final ListenersValidator INSTANCE = new ListenersValidator();

    protected ListenersValidator() {
        super(Listeners.ELEMENT);
    }

    @Override
    public void doValidate(final Listeners that, final ValidationContext context) {
        for(final Listener listener : that.getListeners()) {
            if (listener == null) {
                context.addProblem(Problem.notNullElement("listener"));
                continue;
            }
            ListenerValidator.INSTANCE.validate(listener, context);
        }
    }
}
