package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.PropertyReference;
import io.machinecode.nock.spi.util.Messages;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class PropertyReferenceValidator<T extends PropertyReference> extends ValidatingVisitor<T> {

    protected PropertyReferenceValidator(final String element) {
        super(element);
    }

    @Override
    public void doVisit(final T that, final VisitorNode context) {
        if (that.getRef() == null) {
            context.addProblem(Messages.format("NOCK-002102.validation.required.attribute", "ref"));
        }
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.visit(that.getProperties(), context);
        }
    }
}
