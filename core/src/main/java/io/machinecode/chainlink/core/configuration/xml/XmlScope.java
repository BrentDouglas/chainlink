package io.machinecode.chainlink.core.configuration.xml;

import io.machinecode.chainlink.spi.configuration.ScopeModel;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public class XmlScope {

    @XmlAttribute(name = "factory", required = false)
    protected String factory;

    @XmlElement(name = "job-operator", namespace = XmlChainlink.NAMESPACE, required = false)
    protected List<XmlJobOperator> jobOperators = new ArrayList<>(0);

    public String getFactory() {
        return factory;
    }

    public void setFactory(final String factory) {
        this.factory = factory;
    }

    public List<XmlJobOperator> getJobOperators() {
        return jobOperators;
    }

    public void setJobOperators(final List<XmlJobOperator> jobOperators) {
        this.jobOperators = jobOperators;
    }

    public void configureScope(final ScopeModel model, final ClassLoader loader) throws Exception {
        for (final XmlJobOperator operator : this.jobOperators) {
            operator.configureScope(model, loader);
        }
    }
}
