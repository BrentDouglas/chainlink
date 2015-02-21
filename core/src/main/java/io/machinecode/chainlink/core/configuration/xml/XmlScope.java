package io.machinecode.chainlink.core.configuration.xml;

import io.machinecode.chainlink.core.configuration.ScopeModelImpl;
import io.machinecode.chainlink.spi.schema.JobOperatorSchema;
import io.machinecode.chainlink.spi.schema.JobOperatorWithNameExistsException;
import io.machinecode.chainlink.spi.schema.MutableScopeSchema;
import io.machinecode.chainlink.spi.schema.NoJobOperatorWithNameException;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.ListIterator;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public abstract class XmlScope implements MutableScopeSchema<XmlDeclaration, XmlProperty, XmlJobOperator> {

    @XmlAttribute(name = "ref", required = false)
    protected String ref;

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public void setRef(final String ref) {
        this.ref = ref;
    }

    @Override
    public XmlJobOperator getJobOperator(final String name) {
        for (final XmlJobOperator operator : this.getJobOperators()) {
            if (name.equals(operator.getName())) {
                return operator;
            }
        }
        return null;
    }

    @Override
    public XmlJobOperator removeJobOperator(final String name) throws NoJobOperatorWithNameException {
        final ListIterator<XmlJobOperator> it = this.getJobOperators().listIterator();
        while (it.hasNext()) {
            final XmlJobOperator that = it.next();
            if (name.equals(that.getName())) {
                it.remove();
                return that;
            }
        }
        throw new NoJobOperatorWithNameException("No job operator with name " + name);
    }

    @Override
    public void addJobOperator(final JobOperatorSchema<?,?> jobOperator) throws Exception {
        if (getJobOperator(jobOperator.getName()) != null) {
            throw new JobOperatorWithNameExistsException("A job operator already exists with name " + jobOperator.getName());
        }
        final XmlJobOperator op = new XmlJobOperator();
        op.accept(jobOperator);
        getJobOperators().add(op);
    }

    public void configureScope(final ScopeModelImpl model, final ClassLoader classLoader) throws Exception {
        for (final XmlDeclaration resource : this.getConfigurationLoaders()) {
            model.getConfigurationLoader(resource.getName())
                    .setRef(XmlJobOperator.ref(resource));
        }
        for (final XmlJobOperator operator : this.getJobOperators()) {
            operator.configureScope(model, classLoader);
        }
    }
}
