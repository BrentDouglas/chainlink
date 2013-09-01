package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.Property;
import io.machinecode.nock.spi.util.Message;

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
                context.addProblem(Message.notNullElement("property"));
                continue;
            }
            PropertyValidator.INSTANCE.visit(property, context);
        }
    }
}
