package io.machinecode.chainlink.core.validation;

import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.element.Property;
import io.machinecode.chainlink.spi.util.Messages;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PropertyValidator extends ValidatingVisitor<Property> {

    public static final PropertyValidator INSTANCE = new PropertyValidator();

    protected PropertyValidator() {
        super(Property.ELEMENT);
    }

    @Override
    public void doVisit(final Property that, final VisitorNode context) {
        if (that.getName() == null) {
            context.addProblem(Messages.format("CHAINLINK-002102.validation.required.attribute", "name"));
        }
        if (that.getValue() == null) {
            context.addProblem(Messages.format("CHAINLINK-002102.validation.required.attribute", "value"));
        }
    }
}
