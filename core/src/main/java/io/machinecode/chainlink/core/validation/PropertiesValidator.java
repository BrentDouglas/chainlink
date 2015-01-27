package io.machinecode.chainlink.core.validation;

import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.jsl.Properties;
import io.machinecode.chainlink.spi.jsl.Property;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
