package io.machinecode.chainlink.core.validation;

import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.element.Properties;
import io.machinecode.chainlink.spi.element.Property;
import io.machinecode.chainlink.spi.util.Messages;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertiesValidator extends ValidatingVisitor<Properties> {

    public static final PropertiesValidator INSTANCE = new PropertiesValidator();

    protected PropertiesValidator() {
        super(Properties.ELEMENT);
    }

    @Override
    public void doVisit(final Properties that, final VisitorNode context) {
        for(final Property property : that.getProperties()) {
            if (property == null) {
                context.addProblem(Messages.format("CHAINLINK-002100.validation.not.null.element", "property"));
                continue;
            }
            PropertyValidator.INSTANCE.visit(property, context);
        }
    }
}
