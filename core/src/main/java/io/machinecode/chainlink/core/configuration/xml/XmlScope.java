package io.machinecode.chainlink.core.configuration.xml;

import io.machinecode.chainlink.core.configuration.ScopeModelImpl;
import io.machinecode.chainlink.spi.schema.JobOperatorSchema;
import io.machinecode.chainlink.spi.schema.JobOperatorWithNameExistsException;
import io.machinecode.chainlink.spi.schema.MutableScopeSchema;
import io.machinecode.chainlink.spi.schema.NoJobOperatorWithNameException;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public class XmlScope implements MutableScopeSchema<XmlDeclaration, XmlProperty, XmlJobOperator> {

    @XmlAttribute(name = "ref", required = false)
    protected String ref;

    @XmlElement(name = "configuration-loader", namespace = XmlChainlink.NAMESPACE, required = false)
    private List<XmlDeclaration> configurationLoaders = new ArrayList<>(0);

    @XmlElement(name = "job-operator", namespace = XmlChainlink.NAMESPACE, required = false)
    protected List<XmlJobOperator> jobOperators = new ArrayList<>(0);

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public void setRef(final String ref) {
        this.ref = ref;
    }

    @Override
    public List<XmlDeclaration> getConfigurationLoaders() {
        return configurationLoaders;
    }

    @Override
    public void setConfigurationLoaders(final List<XmlDeclaration> configurationLoaders) {
        this.configurationLoaders = configurationLoaders;
    }

    @Override
    public List<XmlJobOperator> getJobOperators() {
        return jobOperators;
    }

    @Override
    public void setJobOperators(final List<XmlJobOperator> jobOperators) {
        this.jobOperators = jobOperators;
    }

    @Override
    public XmlJobOperator getJobOperator(final String name) {
        for (final XmlJobOperator operator : this.jobOperators) {
            if (name.equals(operator.getName())) {
                return operator;
            }
        }
        return null;
    }

    @Override
    public XmlJobOperator removeJobOperator(final String name) throws NoJobOperatorWithNameException {
        final ListIterator<XmlJobOperator> it = this.jobOperators.listIterator();
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
        jobOperators.add(op);
    }

    public void configureScope(final ScopeModelImpl model, final ClassLoader classLoader) throws Exception {
        for (final XmlDeclaration resource : this.configurationLoaders) {
            model.getConfigurationLoader(resource.getName())
                    .setRef(XmlJobOperator.ref(resource));
        }
        for (final XmlJobOperator operator : this.jobOperators) {
            operator.configureScope(model, classLoader);
        }
    }
}
